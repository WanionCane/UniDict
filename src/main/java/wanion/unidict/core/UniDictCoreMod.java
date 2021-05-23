package wanion.unidict.core;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class UniDictCoreMod implements IFMLLoadingPlugin {
    public static final Logger LOGGER = LogManager.getLogger("UniDictCoreMod");

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "wanion.unidict.core.UniDictCoreModTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
