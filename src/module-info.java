module ca.senecacollege.cpa.app {
    requires javafx.controls;
    requires javafx.fxml;

    opens ca.senecacollege.cpa.app.test to javafx.fxml;
    opens ca.senecacollege.cpa.app.models to javafx.base;
    opens ca.senecacollege.cpa.app.utils to javafx.base;

    exports ca.senecacollege.cpa.app.test;
    exports ca.senecacollege.cpa.app.controllers;
    opens ca.senecacollege.cpa.app.controllers;
}