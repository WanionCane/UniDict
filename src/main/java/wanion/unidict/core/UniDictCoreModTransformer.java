package wanion.unidict.core;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class UniDictCoreModTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraftforge.oredict.OreIngredient")) {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            final FieldNode newField = new FieldNode(Opcodes.ACC_PUBLIC, "oreName", "Ljava/lang/String;", null, null);
            classNode.fields.add(newField);

            for (final MethodNode method : classNode.methods) {
                if (!method.name.equals("<init>") || !method.desc.equals("(Ljava/lang/String;)V"))
                    continue;

                InsnNode lastReturn = null;
                final ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    final AbstractInsnNode instruction = iterator.next();
                    if (instruction instanceof InsnNode && instruction.getOpcode() == Opcodes.RETURN)
                        lastReturn = (InsnNode) instruction;
                }

                if (lastReturn == null) {
                    UniDictCoreMod.LOGGER.error("Failed to transform 'net.minecraftforge.oredict.OreIngredient', failed to find last return.");
                    break;
                }

                method.instructions.insertBefore(lastReturn, getCode());
                UniDictCoreMod.LOGGER.info("Successfully transformed 'net.minecraftforge.oredict.OreIngredient'.");
                break;
            }

            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        else if (transformedName.equals("wanion.unidict.common.Util")) {
            final ClassReader classReader = new ClassReader(basicClass);
            final ClassNode classNode = new ClassNode();
            classReader.accept(classNode, 0);

            for (final MethodNode method : classNode.methods) {
                if (!method.name.equals("getOreNameFromIngredient"))
                    continue;

                method.instructions.clear();
                method.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                method.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/oredict/OreIngredient", "oreName",
                        "Ljava/lang/String;"));
                method.instructions.add(new InsnNode(Opcodes.ARETURN));

                UniDictCoreMod.LOGGER.info("Successfully transformed 'wanion.unidict.common.Util.getOreNameFromIngredient'.");
                break;
            }

            final ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return basicClass;
    }

    private InsnList getCode() {
        final InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraftforge/oredict/OreIngredient", "oreName",
                "Ljava/lang/String;"));
        return list;
    }
}
