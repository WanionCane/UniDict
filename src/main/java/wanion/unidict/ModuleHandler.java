package wanion.unidict;

/*
 * Created by WanionCane(https://github.com/WanionCane).
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 1.1. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/1.1/.
 */

import net.minecraft.launchwrapper.LaunchClassLoader;
import wanion.unidict.common.Reference;
import wanion.unidict.helper.LogHelper;
import wanion.unidict.integration.IntegrationModule;
import wanion.unidict.tweak.TweakModule;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static wanion.unidict.common.Reference.SLASH;

final class ModuleHandler
{
    private final Pattern endsWithClassPattern = Pattern.compile("(\\.class)$");
    private final Pattern expectedClassNamePattern = Pattern.compile("(.+?)(?:[\\-_\\.\\s]).*");
    private final Pattern cleanClassNamePattern = Pattern.compile(".*/(.+\\.class)$");
    private final Pattern slashPattern = Pattern.compile("/");
    private final List<AbstractModule> modules = new ArrayList<>();

    ModuleHandler() {}

    void startModules()
    {
        populateModules();
        if (modules.isEmpty())
            return;
        for (AbstractModule module : modules) {
            module.init();
            if (!module.isEmpty()) {
                module.preparations();
                module.start();
            }
        }
    }

    private void populateModules()
    {
        if (Config.integrationModule)
            modules.add(new IntegrationModule());
        if (Config.tweakModule)
            modules.add(new TweakModule());
        if (Config.loadExternalModules)
            loadExternalModules();
    }

    private void loadExternalModules()
    {
        String modulesFolderName = Reference.MOD_FOLDER + "modules" + SLASH;
        File moduleFolder = new File(modulesFolderName);
        if (!moduleFolder.exists()) {
            if (moduleFolder.mkdir())
                LogHelper.info("Folder modules created.");
            else
                LogHelper.info("Cannot create modules folder.");
            return;
        } else if (moduleFolder.list().length == 0)
            return;
        List<String> jarFilesNames = new ArrayList<>();
        List<JarFile> jarFiles = new ArrayList<>();
        int size = 0;
        for (String folderContent : moduleFolder.list()) {
            if (folderContent.endsWith(".jar")) {
                jarFilesNames.add(folderContent);
                try {
                    jarFiles.add(new JarFile(new File(modulesFolderName + folderContent)));
                } catch (IOException e) { e.printStackTrace(); return; }
                size++;
            }
        }
        if (jarFiles.isEmpty())
            return;
        LaunchClassLoader launchClassLoader = ((LaunchClassLoader) getClass().getClassLoader());
        for (int i = 0; i < size; i++) {
            String jarFileName = jarFilesNames.get(i);
            String expectedClassName = expectedClassNamePattern.matcher(jarFileName).replaceFirst("$1");
            if (Config.specificModuleEnabled(expectedClassName))
                expectedClassName += ".class";
            else
                continue;
            JarFile jarFile = jarFiles.get(i);
            String fullyQualifiedName = null;
            Set<String> expectedClassNames = new HashSet<>(3, 1);
            expectedClassNames.add(expectedClassName);
            if (!jarFileName.contains("Module"))
                expectedClassNames.add(endsWithClassPattern.matcher(expectedClassName).replaceFirst("Module$1"));
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            while (fullyQualifiedName == null && jarEntryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = jarEntryEnumeration.nextElement();
                String jarEntryName = jarEntry.getName();
                Matcher jarEntryMatcher = cleanClassNamePattern.matcher(jarEntryName);
                if (!jarEntryMatcher.find())
                    continue;
                if (expectedClassNames.contains(jarEntryMatcher.group(1)))
                    fullyQualifiedName = endsWithClassPattern.matcher(slashPattern.matcher(jarEntryName).replaceAll(".")).replaceFirst("");
            }
            if (fullyQualifiedName == null) {
                LogHelper.info("Module Handler: Could not find main class in: " + jarFileName + "; Skipping.");
                continue;
            }
            try {
                launchClassLoader.addURL(new File(modulesFolderName + jarFileName).toURI().toURL());
                Class<?> moduleClass = launchClassLoader.loadClass(fullyQualifiedName);
                if (moduleClass.getSuperclass().isAssignableFrom(AbstractModule.class)) {
                    modules.add((AbstractModule) moduleClass.newInstance());
                    LogHelper.info("Module Handler: Loaded Successfully " + jarFileName + " Module.");
                } else
                    LogHelper.error("Module Handler: Cannot load " + jarFileName);
            } catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LogHelper.error("Module Handler: Cannot load " + jarFileName + " " + e);
            }
        }
    }
}