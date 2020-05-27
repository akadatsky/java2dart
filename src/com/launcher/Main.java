package com.launcher;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.dart.engine.ast.AstNode;
import com.google.dart.engine.ast.CompilationUnit;
import com.google.dart.engine.utilities.io.PrintStringWriter;
import com.google.dart.java2dart.Context;
import com.google.dart.java2dart.engine.EngineAnnotationProcessor;
import com.google.dart.java2dart.engine.EngineInstanceOfProcessor;
import com.google.dart.java2dart.engine.EngineSemanticProcessor;
import com.google.dart.java2dart.processor.BeautifySemanticProcessor;
import com.google.dart.java2dart.processor.CollectionSemanticProcessor;
import com.google.dart.java2dart.processor.GuavaSemanticProcessor;
import com.google.dart.java2dart.processor.IOSemanticProcessor;
import com.google.dart.java2dart.processor.JUnitSemanticProcessor;
import com.google.dart.java2dart.processor.ObjectSemanticProcessor;
import com.google.dart.java2dart.processor.PropertySemanticProcessor;
import com.google.dart.java2dart.processor.RenameConstructorsSemanticProcessor;
import com.google.dart.java2dart.processor.SemanticProcessor;
import com.google.dart.java2dart.processor.TypeSemanticProcessor;
import com.google.dart.java2dart.processor.UniqueMemberNamesSemanticProcessor;
import com.google.dart.java2dart.util.ToFormattedSourceVisitor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

public class Main {

    /*
        From https://chromium.googlesource.com/external/dart
        Commit: 532c5163f199b4b20d641211eeac6dfae2472ce9
                Version 1.11.0-dev.0.1
        dart/dart/editor/util/plugins/com.google.dart.java2dart

        Put your java files into inputFolder and get result from resultFolder
     */
    public static void main(String[] args) {
        try {
            File targetFolder = new File("resultFolder");
            File inputFolder = new File("inputFolder");
            Context context = new Context();
            context.addSourceFolder(inputFolder);
            context.addSourceFiles(inputFolder);
            CompilationUnit unit = context.translate();

            runProcessors(context, unit);

            context.ensureUniqueClassMemberNames();
            context.applyLocalVariableSemanticChanges(unit);

            String result = getFormattedSource(unit);

            Files.write(getFormattedSource(unit), new File(targetFolder + "/test.dart"), com.google.common.base.Charsets.UTF_8);

            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runProcessors(Context context, CompilationUnit unit) {
        List<SemanticProcessor> PROCESSORS = ImmutableList.of(
                new TypeSemanticProcessor(context),
                new ObjectSemanticProcessor(context),
                new CollectionSemanticProcessor(context),
                new IOSemanticProcessor(context),
                new PropertySemanticProcessor(context),
                new GuavaSemanticProcessor(context),
                new JUnitSemanticProcessor(context),
                new EngineAnnotationProcessor(context),
                new UniqueMemberNamesSemanticProcessor(context),
                new RenameConstructorsSemanticProcessor(context),
                new EngineSemanticProcessor(context),
                new EngineInstanceOfProcessor(context),
                new BeautifySemanticProcessor(context));
        for (SemanticProcessor processor : PROCESSORS) {
            processor.process(unit);
        }
    }

    static String getFormattedSource(AstNode node) {
        PrintStringWriter writer = new PrintStringWriter();
        node.accept(new ToFormattedSourceVisitor(writer));
        String result = writer.toString();
        return StringUtils.join(StringUtils.split(result, '\n'), "\n");
    }

}
