package com.varanegar.processor;

import com.varanegar.java.util.Currency;
import com.varanegar.processor.annotations.Column;
import com.varanegar.processor.annotations.Property;
import com.varanegar.processor.annotations.Table;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.varanegar.processor.annotations.Table")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class TableAnnotationProcessor extends AbstractProcessor {
    Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Types types = processingEnv.getTypeUtils();
        Set<? extends javax.lang.model.element.Element> elements = roundEnv.getElementsAnnotatedWith(Table.class);
        Set<? extends javax.lang.model.element.Element> columnElements = roundEnv.getElementsAnnotatedWith(Column.class);
        Set<? extends javax.lang.model.element.Element> propertyElements = roundEnv.getElementsAnnotatedWith(Property.class);
        for (Element annotatedElement : elements) {
            if (annotatedElement.getKind() == ElementKind.CLASS) {
                StringBuilder repositoryStringBuilder = new StringBuilder();
                StringBuilder queryBuilderStringBuilder = new StringBuilder();
                StringBuilder cursorMapperStringBuilder = new StringBuilder();
                StringBuilder contentValuesMapperStringBulder = new StringBuilder();
                String simpleModelName = annotatedElement.getSimpleName().toString();
                String longModelName = "";
                String packageName = "";
                Element parentElement = annotatedElement.getEnclosingElement();
                if (parentElement.getKind() == ElementKind.PACKAGE) {
                    packageName = parentElement.toString();
                    longModelName = annotatedElement.getSimpleName().toString();
                } else {
                    longModelName = parentElement.getSimpleName().toString() + "." + annotatedElement.getSimpleName().toString();
                    parentElement = parentElement.getEnclosingElement();
                    if (parentElement.getKind() == ElementKind.PACKAGE) {
                        packageName = parentElement.toString();
                    } else {
                        parentElement = parentElement.getEnclosingElement();
                        longModelName = parentElement.getSimpleName().toString() + "." + longModelName;
                        if (parentElement.getKind() == ElementKind.PACKAGE)
                            packageName = parentElement.toString();
                    }
                }

                String parentClass = "";
                try {
                    List<? extends TypeMirror> superTypes = types.directSupertypes(annotatedElement.asType());
                    if (superTypes != null && !superTypes.isEmpty()) {
                        TypeMirror superClass = superTypes.get(0);
                        parentClass = superClass.toString();
                    }
                } catch (RuntimeException e) {

                }


                messager.printMessage(
                        Diagnostic.Kind.NOTE
                        , "Proccessing Model " + longModelName +
                                " in package " + packageName +
                                " with parent " + parentClass);

                String tblName = simpleModelName.replace("Model", "");
                String projectionName = simpleModelName.replace("Model", "");
                Table tblAnnotation = annotatedElement.getAnnotation(Table.class);
                if (tblAnnotation != null) {
                    if (!tblAnnotation.name().isEmpty()) {
                        tblName = tblAnnotation.name();
                    }
                }

                Generated generatePolicy = Generated.None;
                if (tblAnnotation != null) {
                    generatePolicy = tblAnnotation.uniqueId();
                }

                String generatedCursorMapperClassName = simpleModelName + "CursorMapper";
                String generatedCursorMapperFileName = packageName + "." + simpleModelName + "CursorMapper";
                String generatedContentValueMapperClassName = simpleModelName + "ContentValueMapper";
                String generatedContentValueMapperFileName = packageName + "." + simpleModelName + "ContentValueMapper";
                String generatedQueryBuilderClassName = projectionName;
                String generatedQueryBuilderClassFileName = packageName + "." + projectionName;
                String generatedRepositoryClassName = simpleModelName + "Repository";
                String generatedRepositoryClassFileName = packageName + "." + simpleModelName + "Repository";


                repositoryStringBuilder.append("package " + packageName + ";\n\n")
                        .append("import com.varanegar.framework.base.VaranegarApplication;\n" +
                                "import com.varanegar.framework.database.BaseRepository;\n\n")
                        .append("public class " + generatedRepositoryClassName + " extends BaseRepository<" + longModelName + "> {\n" +

                                "    public " + generatedRepositoryClassName + "() {\n" +
                                "        super(new " + simpleModelName + "CursorMapper(), new " + simpleModelName + "ContentValueMapper());\n")
                        .append("    }")
                        .append("}");


                cursorMapperStringBuilder.append("package " + packageName + ";\n\n")
                        .append("import android.database.Cursor;\n" +
                                "import java.util.UUID;\n" +
                                "import com.varanegar.framework.database.mapper.CursorMapper;\n\n")
                        .append("public class " + generatedCursorMapperClassName + " extends CursorMapper<" + longModelName + "> {\n" +
                                "    @Override\n" +
                                "    public " + longModelName + " map(Cursor cursor) {\n" +
                                "        " + longModelName + " model = new " + longModelName + "();\n\n");

                contentValuesMapperStringBulder.append("package " + packageName + ";\n\n")
                        .append("import android.content.ContentValues;\n" +
                                "\n" +
                                "import com.varanegar.framework.database.mapper.ContentValuesMapper;\n\n")
                        .append("public class " + generatedContentValueMapperClassName + " implements ContentValuesMapper<" + longModelName + "> {\n" +
                                "    @Override\n" +
                                "    public ContentValues map(" + longModelName + " item) {\n" +
                                "        ContentValues cv = new ContentValues();\n");

                queryBuilderStringBuilder.append("package " + packageName + ";\n\n")
                        .append("import com.varanegar.framework.database.model.ModelProjection;\n" +
                                "\n\n")
                        .append("public class " + generatedQueryBuilderClassName + " extends ModelProjection<" + longModelName + "> {\n" +
                                "   protected " + generatedQueryBuilderClassName + "(String name) {\n" +
                                "       super(name);\n" +
                                "   }\n\n");

                contentValuesMapperStringBulder.append("        if (item.UniqueId != null)\n" +
                        "            cv.put(\"UniqueId\", item.UniqueId.toString());\n");
                cursorMapperStringBuilder.append("        if (cursor.getColumnIndex(\"UniqueId\") != -1 && cursor.getString(cursor.getColumnIndex(\"UniqueId\")) != null)\n" +
                        "           model.UniqueId = UUID.fromString(cursor.getString(cursor.getColumnIndex(\"UniqueId\")));");

                for (Element columnElement : columnElements) {
                    String fieldName = columnElement.getSimpleName().toString();
                    String columnName = columnElement.getSimpleName().toString();
                    Column annotation = columnElement.getAnnotation(Column.class);
                    if (annotation != null) {
                        if (!annotation.name().isEmpty()) {
                            columnName = annotation.name();
                        }
                    }

                    String columnType = columnElement.asType().toString();
                    String enclosingModel = columnElement.getEnclosingElement().getSimpleName().toString();
                    String enclosingCanonicalModelName = columnElement.getEnclosingElement().toString();
                    if (enclosingModel.equals(simpleModelName) || enclosingCanonicalModelName.equals(parentClass)) {

                        if (!columnName.equals("rowid")) {
                            if (annotation.isEnum())
                                contentValuesMapperStringBulder.append("        if (item." + fieldName + " != null)\n" +
                                        "            cv.put(\"" + columnName + "\", item." + fieldName + ".ordinal());\n" +
                                        "        else\n" +
                                        "            cv.putNull(\"" + columnName + "\");\n");
                            else if (columnType.equals(UUID.class.getName()))
                                contentValuesMapperStringBulder.append("        if (item." + fieldName + " != null)\n" +
                                        "            cv.put(\"" + columnName + "\", item." + fieldName + ".toString());\n" +
                                        "        else\n" +
                                        "            cv.putNull(\"" + columnName + "\");\n");
                            else if (columnType.equals(Date.class.getName()))
                                contentValuesMapperStringBulder.append("        if (item." + fieldName + " != null)\n" +
                                        "            cv.put(\"" + columnName + "\", item." + fieldName + ".getTime());\n" +
                                        "        else\n" +
                                        "            cv.putNull(\"" + columnName + "\");\n");
                            else if (columnType.equals(BigDecimal.class.getName()))
                                contentValuesMapperStringBulder.append("        if (item." + fieldName + " != null)\n" +
                                        "            cv.put(\"" + columnName + "\", item." + fieldName + ".doubleValue());\n" +
                                        "        else\n" +
                                        "            cv.putNull(\"" + columnName + "\");\n");
                            else if (columnType.equals(Currency.class.getName()))
                                contentValuesMapperStringBulder.append("        if (item." + fieldName + " != null)\n" +
                                        "            cv.put(\"" + columnName + "\", item." + fieldName + ".doubleValue());\n" +
                                        "        else\n" +
                                        "            cv.putNull(\"" + columnName + "\");\n");
//                        else if (columnType.equals(CallActionStatus.class.getName()))
//                            contentValuesMapperStringBulder.append("        if (item." + fieldName + " != null)\n" +
//                                    "            cv.put(\"" + columnName + "\", item." + fieldName + ".getId().toString());\n");
                            else
                                contentValuesMapperStringBulder.append("        cv.put(\"" + columnName + "\", item." + fieldName + ");\n");

                            cursorMapperStringBuilder.append("\n        if (cursor.getColumnIndex(\"" + columnName + "\") == -1)\n" +
                                    "            throw new android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException(\"\\\"" + columnName + "\\\"\\\" is not found in table \\\"" + tblName + "\\\", or you may have projections in the query \");");
                            cursorMapperStringBuilder.append("\n        if (!cursor.isNull(cursor.getColumnIndex(\"" + columnName + "\")))\n");
                            if (annotation.isEnum())
                                cursorMapperStringBuilder.append("            model." + fieldName + " = " + columnType + ".values()[(cursor.getInt(cursor.getColumnIndex(\"" + columnName + "\")))];\n");
                            else if (columnType.equals(UUID.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = UUID.fromString(cursor.getString(cursor.getColumnIndex(\"" + columnName + "\")));\n");
                            else if (columnType.equals(String.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getString(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals(BigDecimal.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = java.math.BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndex(\"" + columnName + "\")));\n");
                            else if (columnType.equals(Currency.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = com.varanegar.java.util.Currency.valueOf(cursor.getDouble(cursor.getColumnIndex(\"" + columnName + "\")));\n");
//                        else if (columnType.equals(CallActionStatus.class.getName()))
//                            cursorMapperStringBuilder.append("            model." + fieldName + " = com.varanegar.java.util.CallActionStatus.get(cursor.getString(cursor.getColumnIndex(\"" + columnName + "\")));\n");
                            else if (columnType.equals(int.class.getName()) || columnType.equals(Integer.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getInt(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals(long.class.getName()) || columnType.equals(Long.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getLong(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals("byte[]") || columnType.equals("Byte[]"))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getBlob(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals(double.class.getName()) || columnType.equals(Double.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getDouble(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals(float.class.getName()) || columnType.equals(Float.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getFloat(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals(short.class.getName()) || columnType.equals(Short.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getShort(cursor.getColumnIndex(\"" + columnName + "\"));\n");
                            else if (columnType.equals(boolean.class.getName()) || columnType.equals(Boolean.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = cursor.getInt(cursor.getColumnIndex(\"" + columnName + "\")) == 0 ? false : true;\n");
                            else if (columnType.equals(Date.class.getName()))
                                cursorMapperStringBuilder.append("            model." + fieldName + " = new java.util.Date(cursor.getLong(cursor.getColumnIndex(\"" + columnName + "\")));\n");
                            cursorMapperStringBuilder.append("        else if(!isNullable(model , \"" + fieldName + "\"))\n" +
                                    "            throw new NullPointerException(\"Null value retrieved for \\\"" + fieldName + "\\\" which is annotated @NotNull\");\n");
                        }
                        queryBuilderStringBuilder.append("   public static " + generatedQueryBuilderClassName + " " +
                                fieldName + " = new " +
                                generatedQueryBuilderClassName + "(\"" + tblName + "." + columnName + "\");\n");
                    }
                }

                queryBuilderStringBuilder.append("   public static " + generatedQueryBuilderClassName + " UniqueId = new " +
                        generatedQueryBuilderClassName + "(\"" + tblName + ".UniqueId\");\n");

                for (Element propertyElement : propertyElements) {
                    String enclosingModel = propertyElement.getEnclosingElement().getSimpleName().toString();
                    if (enclosingModel.equals(simpleModelName)) {
                        String fieldName = propertyElement.getSimpleName().toString();
                        String columnName = propertyElement.getSimpleName().toString();
                        Column annotation = propertyElement.getAnnotation(Column.class);
                        if (annotation != null) {
                            if (!annotation.name().isEmpty()) {
                                columnName = annotation.name();
                            }
                        }

                        if (!columnName.equals("rowid"))
                            queryBuilderStringBuilder.append("   public static " + generatedQueryBuilderClassName + " " +
                                    fieldName + " = new " +
                                    generatedQueryBuilderClassName + "(\"" + columnName + "\");\n");
                    }
                }


                cursorMapperStringBuilder.append("\n        model.setProperties();\n" +
                        "        return model;\n" +
                        "    }\n" +
                        "}");
                contentValuesMapperStringBulder.append("        return  cv;\n" +
                        "    }\n");
                contentValuesMapperStringBulder.append("    @Override\n" +
                        "    public String getTblName() {\n" +
                        "        return \"" + tblName + "\";\n" +
                        "    }\n");
                String generationPolicy = "None";
                if (generatePolicy == Generated.None)
                    generationPolicy = "None";
                else if (generatePolicy == Generated.Insert)
                    generationPolicy = "Insert";
                else if (generatePolicy == Generated.Update)
                    generationPolicy = "Update";
                else if (generatePolicy == Generated.InsertAndUpdate)
                    generationPolicy = "InsertAndUpdate";


                contentValuesMapperStringBulder.append("    @Override\n" +
                        "    public String getUniqueIdGenerationPolicy() {\n" +
                        "        return \"" + generationPolicy + "\";\n" +
                        "    }\n" +
                        "}");
                queryBuilderStringBuilder.append("  public static " + generatedQueryBuilderClassName + " " +
                        projectionName + "Tbl" + " = new " +
                        generatedQueryBuilderClassName + "(\"" + tblName + "\");\n");
                queryBuilderStringBuilder.append("  public static " + generatedQueryBuilderClassName + " " +
                        projectionName + "All" + " = new " +
                        generatedQueryBuilderClassName + "(\"" + tblName + ".*" + "\");\n");
                queryBuilderStringBuilder.append("\n}\n");
                try { // write the file
                    JavaFileObject source = processingEnv.getFiler().createSourceFile(generatedCursorMapperFileName);
                    Writer writer = source.openWriter();
                    writer.write(cursorMapperStringBuilder.toString());
                    writer.flush();
                    writer.close();

                    source = processingEnv.getFiler().createSourceFile(generatedContentValueMapperFileName);
                    writer = source.openWriter();
                    writer.write(contentValuesMapperStringBulder.toString());
                    writer.flush();
                    writer.close();

                    source = processingEnv.getFiler().createSourceFile(generatedQueryBuilderClassFileName);
                    writer = source.openWriter();
                    writer.write(queryBuilderStringBuilder.toString());
                    writer.flush();
                    writer.close();

                    source = processingEnv.getFiler().createSourceFile(generatedRepositoryClassFileName);
                    writer = source.openWriter();
                    writer.write(repositoryStringBuilder.toString());
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // Note: calling e.printStackTrace() will print IO errors
                    // that occur from the file already existing after its first run, this is normal
                }
            }
        }
        return true;
    }
}
