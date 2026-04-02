module com.randexgen.swe1_randexgen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires org.apache.pdfbox;


    opens com.randexgen.swe1_randexgen to javafx.fxml;
    exports com.randexgen.swe1_randexgen.service;
    opens com.randexgen.swe1_randexgen.service to javafx.fxml;
    exports com.randexgen.swe1_randexgen.datamodel;
    opens com.randexgen.swe1_randexgen.datamodel to javafx.fxml;
    exports com.randexgen.swe1_randexgen.controller;
    opens com.randexgen.swe1_randexgen.controller to javafx.fxml;
    exports com.randexgen.swe1_randexgen.app;
    opens com.randexgen.swe1_randexgen.app to javafx.fxml;
}