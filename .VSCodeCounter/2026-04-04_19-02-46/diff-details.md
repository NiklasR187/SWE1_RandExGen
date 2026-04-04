# Diff Details

Date : 2026-04-04 19:02:46

Directory c:\\Users\\nikre\\Documents\\GitHub\\SWE1_RandExGen

Total : 81 files,  1077 codes, 505 comments, 288 blanks, all 1870 lines

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [.idea/encodings.xml](/.idea/encodings.xml) | XML | 7 | 0 | 0 | 7 |
| [.idea/misc.xml](/.idea/misc.xml) | XML | 14 | 0 | 0 | 14 |
| [.idea/vcs.xml](/.idea/vcs.xml) | XML | 6 | 0 | 0 | 6 |
| [.mvn/wrapper/maven-wrapper.properties](/.mvn/wrapper/maven-wrapper.properties) | Java Properties | 2 | 0 | 0 | 2 |
| [mvnw.cmd](/mvnw.cmd) | Batch | 102 | 51 | 36 | 189 |
| [pom.xml](/pom.xml) | XML | 76 | 1 | 5 | 82 |
| [src/main/java/com/randexgen/swe1\_randexgen/app/AppController.java](/src/main/java/com/randexgen/swe1_randexgen/app/AppController.java) | Java | 11 | 12 | 4 | 27 |
| [src/main/java/com/randexgen/swe1\_randexgen/app/HelloApplication.java](/src/main/java/com/randexgen/swe1_randexgen/app/HelloApplication.java) | Java | 25 | 24 | 9 | 58 |
| [src/main/java/com/randexgen/swe1\_randexgen/app/package-info.java](/src/main/java/com/randexgen/swe1_randexgen/app/package-info.java) | Java | 1 | 6 | 0 | 7 |
| [src/main/java/com/randexgen/swe1\_randexgen/controller/PdfviewerController.java](/src/main/java/com/randexgen/swe1_randexgen/controller/PdfviewerController.java) | Java | 288 | 101 | 82 | 471 |
| [src/main/java/com/randexgen/swe1\_randexgen/controller/frame2Controller.java](/src/main/java/com/randexgen/swe1_randexgen/controller/frame2Controller.java) | Java | 907 | 301 | 224 | 1,432 |
| [src/main/java/com/randexgen/swe1\_randexgen/controller/helloViewController.java](/src/main/java/com/randexgen/swe1_randexgen/controller/helloViewController.java) | Java | 109 | 60 | 29 | 198 |
| [src/main/java/com/randexgen/swe1\_randexgen/controller/package-info.java](/src/main/java/com/randexgen/swe1_randexgen/controller/package-info.java) | Java | 1 | 6 | 0 | 7 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/Chapter.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/Chapter.java) | Java | 71 | 89 | 22 | 182 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/DifficultyLevel.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/DifficultyLevel.java) | Java | 6 | 0 | 2 | 8 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/Exam.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/Exam.java) | Java | 37 | 53 | 12 | 102 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/ExamAppearance.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/ExamAppearance.java) | Java | 5 | 0 | 3 | 8 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/ExamType.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/ExamType.java) | Java | 5 | 0 | 2 | 7 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/Subtask.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/Subtask.java) | Java | 60 | 85 | 18 | 163 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/Variant.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/Variant.java) | Java | 46 | 64 | 13 | 123 |
| [src/main/java/com/randexgen/swe1\_randexgen/datamodel/package-info.java](/src/main/java/com/randexgen/swe1_randexgen/datamodel/package-info.java) | Java | 1 | 6 | 0 | 7 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/AppState.java](/src/main/java/com/randexgen/swe1_randexgen/service/AppState.java) | Java | 23 | 35 | 9 | 67 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/DataValidator.java](/src/main/java/com/randexgen/swe1_randexgen/service/DataValidator.java) | Java | 119 | 102 | 35 | 256 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/ExamGenerator.java](/src/main/java/com/randexgen/swe1_randexgen/service/ExamGenerator.java) | Java | 236 | 192 | 66 | 494 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/ExportMode.java](/src/main/java/com/randexgen/swe1_randexgen/service/ExportMode.java) | Java | 5 | 12 | 3 | 20 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/GeneratedTask.java](/src/main/java/com/randexgen/swe1_randexgen/service/GeneratedTask.java) | Java | 23 | 29 | 7 | 59 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/PDFExporter.java](/src/main/java/com/randexgen/swe1_randexgen/service/PDFExporter.java) | Java | 288 | 208 | 87 | 583 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/ScoreCalculator.java](/src/main/java/com/randexgen/swe1_randexgen/service/ScoreCalculator.java) | Java | 122 | 81 | 37 | 240 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/XMLFileValidator.java](/src/main/java/com/randexgen/swe1_randexgen/service/XMLFileValidator.java) | Java | 106 | 100 | 43 | 249 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/XMLParser.java](/src/main/java/com/randexgen/swe1_randexgen/service/XMLParser.java) | Java | 82 | 65 | 37 | 184 |
| [src/main/java/com/randexgen/swe1\_randexgen/service/package-info.java](/src/main/java/com/randexgen/swe1_randexgen/service/package-info.java) | Java | 1 | 6 | 0 | 7 |
| [src/main/java/module-info.java](/src/main/java/module-info.java) | Java | 15 | 0 | 2 | 17 |
| [src/main/resources/com/randexgen/swe1\_randexgen/Frame2.fxml](/src/main/resources/com/randexgen/swe1_randexgen/Frame2.fxml) | XML | 110 | 1 | 18 | 129 |
| [src/main/resources/com/randexgen/swe1\_randexgen/hello-view.fxml](/src/main/resources/com/randexgen/swe1_randexgen/hello-view.fxml) | XML | 97 | 5 | 13 | 115 |
| [src/main/resources/com/randexgen/swe1\_randexgen/pdfviewer.fxml](/src/main/resources/com/randexgen/swe1_randexgen/pdfviewer.fxml) | XML | 175 | 2 | 22 | 199 |
| [src/main/resources/com/randexgen/swe1\_randexgen/styles.css](/src/main/resources/com/randexgen/swe1_randexgen/styles.css) | PostCSS | 148 | 8 | 29 | 185 |
| [src/main/resources/com/randexgen/swe1\_randexgen/styles2.css](/src/main/resources/com/randexgen/swe1_randexgen/styles2.css) | PostCSS | 172 | 32 | 28 | 232 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/AppStateTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/AppStateTest.java) | Java | 27 | 23 | 13 | 63 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/DataValidatorTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/DataValidatorTest.java) | Java | 215 | 133 | 72 | 420 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/ExamGeneratorTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/ExamGeneratorTest.java) | Java | 176 | 92 | 49 | 317 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/PDFExporterTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/PDFExporterTest.java) | Java | 115 | 40 | 41 | 196 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/ScoreCalculatorTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/ScoreCalculatorTest.java) | Java | 125 | 69 | 42 | 236 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/XMLFileValidatorTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/XMLFileValidatorTest.java) | Java | 198 | 74 | 28 | 300 |
| [src/test/java/com/randexgen/swe1\_randexgen/service/XMLParserTest.java](/src/test/java/com/randexgen/swe1_randexgen/service/XMLParserTest.java) | Java | 167 | 50 | 28 | 245 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\.idea\\encodings.xml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5C.idea%5Cencodings.xml) | XML | -7 | 0 | 0 | -7 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\.idea\\misc.xml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5C.idea%5Cmisc.xml) | XML | -14 | 0 | 0 | -14 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\.idea\\vcs.xml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5C.idea%5Cvcs.xml) | XML | -6 | 0 | 0 | -6 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\.mvn\\wrapper\\maven-wrapper.properties](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5C.mvn%5Cwrapper%5Cmaven-wrapper.properties) | Java Properties | -2 | 0 | 0 | -2 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\mvnw.cmd](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Cmvnw.cmd) | Batch | -102 | -51 | -36 | -189 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\pom.xml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Cpom.xml) | XML | -76 | -1 | -5 | -82 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\app\\AppController.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Capp%5CAppController.java) | Java | -11 | -12 | -4 | -27 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\app\\HelloApplication.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Capp%5CHelloApplication.java) | Java | -25 | -24 | -9 | -58 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\app\\package-info.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Capp%5Cpackage-info.java) | Java | -1 | -6 | 0 | -7 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\controller\\PdfviewerController.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Ccontroller%5CPdfviewerController.java) | Java | -288 | -101 | -82 | -471 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\controller\\frame2Controller.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Ccontroller%5Cframe2Controller.java) | Java | -878 | -278 | -214 | -1,370 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\controller\\helloViewController.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Ccontroller%5ChelloViewController.java) | Java | -109 | -60 | -29 | -198 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\controller\\package-info.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Ccontroller%5Cpackage-info.java) | Java | -1 | -6 | 0 | -7 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\Chapter.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CChapter.java) | Java | -71 | -89 | -22 | -182 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\DifficultyLevel.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CDifficultyLevel.java) | Java | -6 | 0 | -2 | -8 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\Exam.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CExam.java) | Java | -37 | -53 | -12 | -102 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\ExamAppearance.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CExamAppearance.java) | Java | -5 | 0 | -3 | -8 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\ExamType.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CExamType.java) | Java | -5 | 0 | -2 | -7 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\Subtask.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CSubtask.java) | Java | -60 | -85 | -18 | -163 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\Variant.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5CVariant.java) | Java | -46 | -64 | -13 | -123 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\datamodel\\package-info.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cdatamodel%5Cpackage-info.java) | Java | -1 | -6 | 0 | -7 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\AppState.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CAppState.java) | Java | -23 | -35 | -9 | -67 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\DataValidator.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CDataValidator.java) | Java | -119 | -102 | -35 | -256 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\ExamGenerator.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CExamGenerator.java) | Java | -236 | -192 | -66 | -494 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\ExportMode.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CExportMode.java) | Java | -5 | -12 | -3 | -20 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\GeneratedTask.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CGeneratedTask.java) | Java | -23 | -29 | -7 | -59 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\PDFExporter.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CPDFExporter.java) | Java | -288 | -208 | -87 | -583 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\ScoreCalculator.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CScoreCalculator.java) | Java | -122 | -81 | -37 | -240 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\XMLFileValidator.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CXMLFileValidator.java) | Java | -106 | -100 | -43 | -249 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\XMLParser.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5CXMLParser.java) | Java | -82 | -65 | -37 | -184 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\com\\randexgen\\swe1\_randexgen\\service\\package-info.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cservice%5Cpackage-info.java) | Java | -1 | -6 | 0 | -7 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\java\\module-info.java](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cjava%5Cmodule-info.java) | Java | -15 | 0 | -2 | -17 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\resources\\com\\randexgen\\swe1\_randexgen\\Frame2.fxml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cresources%5Ccom%5Crandexgen%5Cswe1_randexgen%5CFrame2.fxml) | XML | -84 | 0 | -13 | -97 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\resources\\com\\randexgen\\swe1\_randexgen\\hello-view.fxml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cresources%5Ccom%5Crandexgen%5Cswe1_randexgen%5Chello-view.fxml) | XML | -97 | -5 | -13 | -115 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\resources\\com\\randexgen\\swe1\_randexgen\\pdfviewer.fxml](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cresources%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cpdfviewer.fxml) | XML | -176 | -2 | -22 | -200 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\resources\\com\\randexgen\\swe1\_randexgen\\styles.css](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cresources%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cstyles.css) | PostCSS | -148 | -8 | -29 | -185 |
| [c:\\Users\\nikre\\IdeaProjects\\SWE1\_RandExGen\\src\\main\\resources\\com\\randexgen\\swe1\_randexgen\\styles2.css](/c:%5CUsers%5Cnikre%5CIdeaProjects%5CSWE1_RandExGen%5Csrc%5Cmain%5Cresources%5Ccom%5Crandexgen%5Cswe1_randexgen%5Cstyles2.css) | PostCSS | -172 | -32 | -28 | -232 |

[Summary](results.md) / [Details](details.md) / [Diff Summary](diff.md) / Diff Details