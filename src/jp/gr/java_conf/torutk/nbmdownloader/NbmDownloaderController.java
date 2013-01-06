/*
 * Copyright (c) 2013, toru
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.gr.java_conf.torutk.nbmdownloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

/**
 * GUIコントローラクラス。
 * 
 * @author toru
 */
public class NbmDownloaderController implements Initializable {
    
    @FXML
    private TextField urlField;
    @FXML
    private TextField directoryField;
    @FXML
    private TableView<Module> table;
    @FXML
    private TableColumn<Module, Integer> noColumn;
    @FXML
    private TableColumn<Module, String> nameColumn;
    @FXML
    private TableColumn<Module, String> statusColumn;
    @FXML
    private HBox hBox;
    private ProgressBar progressBar;
    
    private List<Module> modules = new ArrayList<>();
    private UpdateCenter updateCenter;
    
    @FXML
    private void handleStartButtonAction(ActionEvent event) {
        
        urlField.getStyleClass().removeAll("text-field", "text-field-error");
        String text = urlField.getText();
        try {
            final URL url = new URL(text);
            Task<Void> prepareTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    updateCenter = new UpdateCenter(url);
                    modules = createModules(updateCenter);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            table.getItems().clear();
                            table.getItems().addAll(modules);
                            download();
                        }
                    });
                    return null;
                }
            };
            Thread thr = new Thread(prepareTask);
            thr.setDaemon(true);
            thr.start();
        } catch (MalformedURLException ex) {
            urlField.getStyleClass().add("text-field-error");
            // TODO 
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private List<Module> createModules(UpdateCenter updateCenter) {
        List<Module> modules = new ArrayList<>();
        List<String> moduleNames = updateCenter.getModules();
        for (int i = 0; i < moduleNames.size(); i++) {
            modules.add(new Module(i + 1, moduleNames.get(i), "yet", updateCenter.getUrl(moduleNames.get(i))));
        }
        return modules;
    }
    
    private void download() {
        final Path path = Paths.get(directoryField.getText());
        System.out.println("Now download files to " + path);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (Module module : modules) {
                    changeStatusOnAnotherThread(module, "doing");
                    UrlDownloader.download(module.getUrl(), path);
                    changeStatusOnAnotherThread(module, "done");
                }
                return null;
            }
        };
        Thread thr = new Thread(task);
        thr.setDaemon(true);
        thr.start();
    }

    private void changeStatusOnAnotherThread(final Module module, final String status) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                module.setStatus(status);
            }
        });
    }
    
    @FXML
    private void handleDirectoryButtonAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select directory");
        File path = chooser.showDialog(null);
        if (path != null) {
            directoryField.setText(path.getAbsolutePath());
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        noColumn.setCellValueFactory(new PropertyValueFactory<Module, Integer>("no"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Module, String>("name"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<Module, String>("status"));
        
        // test data
        ObservableList<Module> list = table.getItems();
        list.add(new Module(1, "com-android-ide_common.nmb", "done", null));
        list.add(new Module(2, "com-android-layoutlib_api.nbm", "downloading", null));
        list.add(new Module(3, "net-sf-kxml-kxml2.nbm", "waiting", null));
        
    }    
}
