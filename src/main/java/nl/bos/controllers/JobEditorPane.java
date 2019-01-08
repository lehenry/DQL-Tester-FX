package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfTime;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.DateTimePicker;
import nl.bos.JobMonitor;
import nl.bos.MyJobObject;
import nl.bos.Repository;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static nl.bos.Constants.*;

public class JobEditorPane implements Initializable, ChangeListener {
    private static final Logger log = Logger.getLogger(JobEditorPane.class.getName());

    private static final String MINUTES = "Minutes";
    private static final String HOURS = "Hours";
    private static final String DAYS = "Days";
    private static final String WEEKS = "Weeks";
    private static final String MONTHS = "Months";
    private static final String YEARS = "Years";
    private static final String DAY_OF_WEEK = "Day Of Week";
    private static final String DAY_OF_MONTH = "Day Of Month";
    private static final String DAY_OF_YEAR = "Day Of Year";

    private static final Image RUNNING = new Image("nl/bos/icons/running.png");
    private static final Image ACTIVE = new Image("nl/bos/icons/active.png");
    private static final Image INACTIVE = new Image("nl/bos/icons/inactive.png");
    private static final Image RUNNING_ANIM = new Image("nl/bos/icons/running.gif");
    private static final Image ACTIVE_ANIM = new Image("nl/bos/icons/active.gif");
    private static final Image INACTIVE_ANIM = new Image("nl/bos/icons/inactive.gif");
    private static final String ANY_RUNNING_SERVER = "Any Running Server";

    private Repository repository = Repository.getInstance();
    private String currentCategory;
    private JobMonitor jobMonitor;
    private MyJobObject currentJob;

    @FXML
    private HBox hbButtons;
    @FXML
    private VBox vbJobList;
    @FXML
    private VBox vbFieldButtons;
    @FXML
    private VBox vbFields;
    @FXML
    private VBox vbFieldLabels;
    @FXML
    private Button btnViewLog;
    @FXML
    private ListView lvJobs;
    @FXML
    private ChoiceBox cbJobsFilter;
    @FXML
    private TextField txtObjectId;
    @FXML
    private TextField txtLastCompletionDate;
    @FXML
    private TextField txtNextInvocationDate;
    @FXML
    private TextField txtRunCompleted;
    @FXML
    private TextField txtLastReturnCode;
    @FXML
    private CheckBox chkIsContinued;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtDescription;
    @FXML
    private RadioButton rbStateActive;
    @FXML
    private RadioButton rbStateInactive;
    @FXML
    private ComboBox cbTraceLevel;
    @FXML
    private CheckBox chkDeactivateOnFailure;
    @FXML
    private ComboBox cbDesignatedServer;
    @FXML
    private DateTimePicker dpStartDate;
    @FXML
    private ComboBox cbRepeat;
    @FXML
    private TextField txtFrequency;
    @FXML
    private TextField txtContinuousInterval;
    @FXML
    private CheckBox cbPassStandardArguments;
    @FXML
    private TextField txtMethod;
    @FXML
    private ListView lvArguments;
    @FXML
    private TextField txtRunning;
    @FXML
    private ImageView ivState;
    @FXML
    private TextField txtNrOfArguments;
    @FXML
    private Label lblStatus;
    @FXML
    private DateTimePicker dpEndDate;
    @FXML
    private TextField txtMaxIterations;
    @FXML
    private RadioButton rbEndDate;
    @FXML
    private RadioButton rbEndMaxIterations;
    @FXML
    private TextField txtNrOfJobsListed;
    @FXML
    private Button btnEditArguments;
    @FXML
    private Button btnBrowseMethod;
    @FXML
    private Button btnEditServer;
    @FXML
    private Button btnNewJob;
    @FXML
    private Button btnExportJob;
    @FXML
    private Button btnDeleteJob;
    @FXML
    private Button btnCopyJob;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnExit;
    @FXML
    private CheckBox cbWatchJob;
    @FXML
    private ImageView ivLock;
    @FXML
    private CheckBox cbRunAfterUpdate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ObservableList jobIds = FXCollections.observableArrayList();
            ObservableList categories = FXCollections.observableArrayList();
            IDfCollection collection = repository.query("select r_object_id, title, object_name, is_inactive, a_current_status from dm_job order by title, object_name");
            while (collection.next()) {
                String type = collection.getString(ATTR_TITLE);
                jobIds.add(new MyJobObject(collection.getString(ATTR_R_OBJECT_ID), collection.getString(ATTR_OBJECT_NAME), type, !collection.getBoolean(ATTR_IS_INACTIVE), collection.getString(ATTR_A_CURRENT_STATUS)));
                if (!categories.contains(type))
                    categories.add(type);
            }
            collection.close();
            lvJobs.setItems(jobIds);
            txtNrOfJobsListed.setText(String.valueOf(jobIds.size()));
            lvJobs.setCellFactory(param -> new ListCell<MyJobObject>() {
                private ImageView imageView = new ImageView();

                @Override
                protected void updateItem(MyJobObject item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (item.isRunning())
                            imageView.setImage(RUNNING);
                        else if (item.isActive())
                            imageView.setImage(ACTIVE);
                        else
                            imageView.setImage(INACTIVE);
                        setText(item.getObjectName());
                        setGraphic(imageView);
                    }
                }
            });

            cbJobsFilter.setItems(categories);

            ObservableList levels = FXCollections.observableArrayList();
            for (int i = 0; i <= 10; i++) {
                levels.add(i);
            }
            cbTraceLevel.setItems(levels);

            ObservableList servers = FXCollections.observableArrayList();
            servers.add(ANY_RUNNING_SERVER);
            IDfCollection serverInfo = repository.query("select object_name, r_host_name from dm_server_config order by object_name");
            while (serverInfo.next()) {
                servers.add(String.format("%s.%s@%s", repository.getRepositoryName(), serverInfo.getString(ATTR_OBJECT_NAME), serverInfo.getString(ATTR_R_HOST_NAME)));
            }
            serverInfo.close();
            cbDesignatedServer.setItems(servers);

            ObservableList repeats = FXCollections.observableArrayList();
            repeats.add(MINUTES);
            repeats.add(HOURS);
            repeats.add(DAYS);
            repeats.add(WEEKS);
            repeats.add(MONTHS);
            repeats.add(YEARS);
            repeats.add(DAY_OF_WEEK);
            repeats.add(DAY_OF_MONTH);
            repeats.add(DAY_OF_YEAR);
            cbRepeat.setItems(repeats);
        } catch (DfException e) {
            log.finest(e.getMessage());
        }

        lvJobs.getSelectionModel().selectedItemProperty().addListener(this);
        cbJobsFilter.getSelectionModel().selectedItemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        log.info(String.format("ListView selection changed from old value %s to the new value %s", oldValue, newValue));
        if (observable.getValue() != null) {
            if (observable.getValue().getClass().equals(MyJobObject.class)) {
                currentJob = (MyJobObject) observable.getValue();
                btnCopyJob.setDisable(false);
                btnDeleteJob.setDisable(false);
                btnExportJob.setDisable(false);
                vbFieldLabels.setDisable(false);
                vbFields.setDisable(false);
                vbFieldButtons.setDisable(false);

                try {
                    IDfPersistentObject job = repository.getSession().getObject(new DfId(((MyJobObject) observable.getValue()).getObjectId()));
                    txtObjectId.setText(job.getString(ATTR_R_OBJECT_ID));
                    txtLastCompletionDate.setText(job.getString(ATTR_A_LAST_COMPLETION));
                    txtNextInvocationDate.setText(job.getString(ATTR_A_NEXT_INVOCATION));
                    txtRunCompleted.setText(String.valueOf(job.getInt(ATTR_A_ITERATIONS)));
                    txtLastReturnCode.setText(job.getString(ATTR_A_LAST_RETURN_CODE));
                    chkIsContinued.setDisable(job.getBoolean(ATTR_A_IS_CONTINUED));
                    txtName.setText(job.getString(ATTR_OBJECT_NAME));
                    txtType.setText(job.getString(ATTR_TITLE));
                    txtDescription.setText(job.getString(ATTR_SUBJECT));
                    rbStateActive.setSelected(!job.getBoolean(ATTR_IS_INACTIVE));
                    rbStateInactive.setSelected(job.getBoolean(ATTR_IS_INACTIVE));
                    cbTraceLevel.setValue(job.getInt(ATTR_METHOD_TRACE_LEVEL));
                    chkDeactivateOnFailure.setSelected(job.getBoolean(ATTR_INACTIVATE_AFTER_FAILURE));
                    String targetServer = job.getString(ATTR_TARGET_SERVER);
                    if (targetServer.isEmpty())
                        cbDesignatedServer.setValue(ANY_RUNNING_SERVER);
                    else
                        cbDesignatedServer.setValue(targetServer);
                    cbRunAfterUpdate.setSelected(job.getBoolean(ATTR_RUN_NOW));

                    IDfTime startDate = job.getTime(ATTR_START_DATE);
                    if (!startDate.isNullDate()) {
                        LocalDateTime localDate = LocalDateTime.of(startDate.getYear(), startDate.getMonth(), startDate.getDay(), startDate.getHour(), startDate.getMinutes(), startDate.getSeconds());
                        dpStartDate.setDateTimeValue(localDate);
                    } else
                        dpStartDate.setDateTimeValue(LocalDateTime.now());

                    cbRepeat.setValue(getDisplayValue(job.getInt(ATTR_RUN_MODE)));
                    txtFrequency.setText(String.valueOf(job.getInt(ATTR_RUN_INTERVAL)));
                    txtContinuousInterval.setText(String.valueOf(job.getInt(ATTR_A_CONTINUATION_INTERVAL)));
                    cbPassStandardArguments.setSelected(job.getBoolean(ATTR_PASS_STANDARD_ARGUMENTS));
                    txtMethod.setText(job.getString(ATTR_METHOD_NAME));

                    ObservableList arguments = FXCollections.observableArrayList();
                    int methodArguments = job.getValueCount(ATTR_METHOD_ARGUMENTS);
                    for (int i = 0; i < methodArguments; i++) {
                        arguments.add(job.getRepeatingString(ATTR_METHOD_ARGUMENTS, i));
                    }
                    lvArguments.setItems(arguments);

                    txtNrOfArguments.setText(String.valueOf(methodArguments));

                    lblStatus.setText(job.getString(ATTR_A_CURRENT_STATUS));
                    updateStatus(job.getString(ATTR_R_LOCK_OWNER), job.getBoolean(ATTR_IS_INACTIVE));

                    IDfTime endDate = job.getTime(ATTR_EXPIRATION_DATE);
                    if (!endDate.isNullDate()) {
                        LocalDateTime localEndDate = LocalDateTime.of(endDate.getYear(), endDate.getMonth(), endDate.getDay(), endDate.getHour(), endDate.getMinutes(), endDate.getSeconds());
                        dpEndDate.setDateTimeValue(localEndDate);
                    } else
                        dpEndDate.setDateTimeValue(LocalDateTime.now());

                    txtMaxIterations.setText(String.valueOf(job.getInt(ATTR_MAX_ITERATIONS)));
                    txtMaxIterations.setDisable(!hasEndIterationValue(job));

                    rbEndDate.setSelected(!hasEndIterationValue(job));
                    rbEndMaxIterations.setSelected(hasEndIterationValue(job));
                } catch (DfException e) {
                    log.finest(e.getMessage());
                }
            } else {
                log.info(String.format("Category is %s", observable));
                ObservableList jobIds = FXCollections.observableArrayList();
                try {
                    IDfCollection collection;
                    currentCategory = (String) observable.getValue();
                    if (!currentCategory.equals(""))
                        collection = repository.query(String.format("select r_object_id, title, object_name, is_inactive, a_current_status from dm_job where title = '%s' order by title, object_name", currentCategory));
                    else
                        collection = repository.query("select r_object_id, title, object_name, is_inactive, a_current_status from dm_job order by title, object_name");
                    while (collection.next()) {
                        jobIds.add(new MyJobObject(collection.getString(ATTR_R_OBJECT_ID), collection.getString(ATTR_OBJECT_NAME), collection.getString(ATTR_TITLE), !collection.getBoolean(ATTR_IS_INACTIVE), collection.getString(ATTR_A_CURRENT_STATUS)));
                    }
                    collection.close();
                } catch (DfException e) {
                    log.finest(e.getMessage());
                }
                lvJobs.setItems(jobIds);
                txtNrOfJobsListed.setText(String.valueOf(jobIds.size()));
            }
        }
        btnUpdate.setDisable(true);
    }

    private boolean hasEndIterationValue(IDfPersistentObject job) throws DfException {
        return job.getInt(ATTR_MAX_ITERATIONS) != 0;
    }

    private String getDisplayValue(int runMode) {
        switch (runMode) {
            case 1:
                return MINUTES;
            case 2:
                return HOURS;
            case 3:
                return DAYS;
            case 4:
                return WEEKS;
            case 5:
                return MONTHS;
            case 6:
                return YEARS;
            case 7:
                return DAY_OF_WEEK;
            case 8:
                return DAY_OF_MONTH;
            case 9:
                return DAY_OF_YEAR;
            default:
                return "Unknown";
        }
    }

    private int getRunValue(String runMode) {
        switch (runMode) {
            case MINUTES:
                return 1;
            case HOURS:
                return 2;
            case DAYS:
                return 3;
            case WEEKS:
                return 4;
            case MONTHS:
                return 5;
            case YEARS:
                return 6;
            case DAY_OF_WEEK:
                return 7;
            case DAY_OF_MONTH:
                return 8;
            case DAY_OF_YEAR:
                return 9;
            default:
                return -1;
        }
    }

    @FXML
    private void handleExit(ActionEvent actionEvent) {
        log.info(String.valueOf(actionEvent.getSource()));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleRefresh(ActionEvent actionEvent) {
        ObservableList jobIds = FXCollections.observableArrayList();
        try {
            IDfCollection collection;
            if (currentCategory != null && !currentCategory.isEmpty())
                collection = repository.query(String.format("select r_object_id, title, object_name, is_inactive, a_current_status from dm_job where title = '%s' order by title, object_name", currentCategory));
            else
                collection = repository.query("select r_object_id, title, object_name, is_inactive, a_current_status from dm_job order by title, object_name");
            while (collection.next()) {
                jobIds.add(new MyJobObject(collection.getString(ATTR_R_OBJECT_ID), collection.getString(ATTR_OBJECT_NAME), collection.getString(ATTR_TITLE), !collection.getBoolean(ATTR_IS_INACTIVE), collection.getString(ATTR_A_CURRENT_STATUS)));
            }
            collection.close();
        } catch (DfException e) {
            log.finest(e.getMessage());
        }
        lvJobs.setItems(jobIds);
        txtNrOfJobsListed.setText(String.valueOf(jobIds.size()));
    }

    @FXML
    private void handleWatchJob(ActionEvent actionEvent) {
        if (cbWatchJob.isSelected()) {
            btnViewLog.setDisable(true);
            vbFields.setDisable(true);
            vbFieldButtons.setDisable(true);
            vbJobList.setDisable(true);
            hbButtons.setDisable(true);
            Stage stage = (Stage) vbJobList.getScene().getWindow();
            stage.setTitle(String.format("Job Editor - %s [Watch Mode]", repository.getRepositoryName()));
            jobMonitor = new JobMonitor(currentJob, this);
            new Thread(jobMonitor).start();
        } else {
            btnViewLog.setDisable(false);
            vbFields.setDisable(false);
            vbFieldButtons.setDisable(false);
            vbJobList.setDisable(false);
            hbButtons.setDisable(false);
            Stage stage = (Stage) vbJobList.getScene().getWindow();
            stage.setTitle(String.format("Job Editor - %s", repository.getRepositoryName()));
            jobMonitor.stop();
        }
    }

    private void updateStatus(String lockOwner, boolean isInactive) {
        if (!lockOwner.isEmpty()) {
            ivState.setImage(RUNNING_ANIM);
            txtRunning.setText("RUNNING");
        } else if (!isInactive) {
            ivState.setImage(ACTIVE_ANIM);
            txtRunning.setText("");
        } else {
            ivState.setImage(INACTIVE_ANIM);
            txtRunning.setText("");
        }
    }

    public void updateFields(IDfPersistentObject job) throws DfException {
        txtLastCompletionDate.setText(job.getString(ATTR_A_LAST_COMPLETION));
        cbRunAfterUpdate.setSelected(job.getBoolean(ATTR_RUN_NOW));
        txtRunCompleted.setText(String.valueOf(job.getInt(ATTR_A_ITERATIONS)));
        lblStatus.setText(job.getString(ATTR_A_CURRENT_STATUS));

        if (job.getString(ATTR_R_LOCK_OWNER).isEmpty())
            ivLock.setVisible(false);
        else
            ivLock.setVisible(true);

        updateStatus(job.getString(ATTR_R_LOCK_OWNER), job.getBoolean(ATTR_IS_INACTIVE));
    }

    @FXML
    private void handleRunAfterUpdate(ActionEvent actionEvent) {
        currentJob.updateChanges(ATTR_RUN_NOW, String.format("set %s = %s", ATTR_RUN_NOW, cbRunAfterUpdate.isSelected()));
        btnUpdate.setDisable(false);
        cbWatchJob.setDisable(true);
    }

    @FXML
    private void handleUpdate(ActionEvent actionEvent) throws DfException {
        String updateList = currentJob.getUpdateList();
        repository.query(String.format("update dm_job object %s where r_object_id = '%s'", updateList, currentJob.getObjectId()));
        btnUpdate.setDisable(true);
        cbWatchJob.setDisable(false);
    }

    @FXML
    private void handleTextField(KeyEvent keyEvent) {
        if (((TextField) keyEvent.getSource()).getId().equals("txtDescription"))
            currentJob.updateChanges(ATTR_SUBJECT, String.format("set %s = '%s'", ATTR_SUBJECT, txtDescription.getText()));
        if (((TextField) keyEvent.getSource()).getId().equals("txtName"))
            currentJob.updateChanges(ATTR_OBJECT_NAME, String.format("set %s = '%s'", ATTR_OBJECT_NAME, txtName.getText()));
        if (((TextField) keyEvent.getSource()).getId().equals("txtType"))
            currentJob.updateChanges(ATTR_TITLE, String.format("set %s = '%s'", ATTR_TITLE, txtType.getText()));
        btnUpdate.setDisable(false);
        cbWatchJob.setDisable(true);
    }

    @FXML
    private void handleViewLog(ActionEvent actionEvent) throws DfException, IOException {
        IDfCollection query = repository.query(String.format("select r_object_id, object_name from dm_document where folder('/Temp/Jobs/%s') order by r_creation_date desc enable(return_top 1)", currentJob.getObjectName()));
        while (query.next()) {
            log.info(query.getString(ATTR_R_OBJECT_ID));
            IDfDocument jobLog = (IDfDocument) repository.getSession().getObject(new DfId(query.getString(ATTR_R_OBJECT_ID)));
            ByteArrayInputStream jobLogContent = jobLog.getContent();

            File tempFile = File.createTempFile("tmp_", ".txt");
            log.info(tempFile.getPath());
            ReadableByteChannel readableByteChannel = Channels.newChannel(jobLogContent);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

            jobLogContent.close();
            readableByteChannel.close();
            fileChannel.close();

            if (!Desktop.isDesktopSupported()) {
                log.info("Desktop is not supported");
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            if (tempFile.exists())
                desktop.open(tempFile);
        }
    }

    @FXML
    private void handleToggleState(ActionEvent actionEvent) {
        if (((RadioButton) actionEvent.getSource()).getId().equals("rbStateActive")) {
            currentJob.updateChanges(ATTR_IS_INACTIVE, String.format("set %s = %s", ATTR_IS_INACTIVE, rbStateActive.isSelected()));
        }
        if (((RadioButton) actionEvent.getSource()).getId().equals("rbStateInactive")) {
            currentJob.updateChanges(ATTR_IS_INACTIVE, String.format("set %s = %s", ATTR_IS_INACTIVE, !rbStateInactive.isSelected()));
        }
        btnUpdate.setDisable(false);
        cbWatchJob.setDisable(true);
    }

    @FXML
    private void handleComboBox(ActionEvent actionEvent) {
        if (((ComboBox) actionEvent.getSource()).getId().equals("cbTraceLevel")) {
            currentJob.updateChanges(ATTR_METHOD_TRACE_LEVEL, String.format("set %s = %s", ATTR_METHOD_TRACE_LEVEL, cbTraceLevel.getValue()));
        }
        if (((ComboBox) actionEvent.getSource()).getId().equals("cbDesignatedServer")) {
            String serverValue = cbDesignatedServer.getValue().toString();
            if (serverValue.equals(ANY_RUNNING_SERVER))
                serverValue = "";
            currentJob.updateChanges(ATTR_TARGET_SERVER, String.format("set %s = '%s'", ATTR_TARGET_SERVER, serverValue));
        }
        if (((ComboBox) actionEvent.getSource()).getId().equals("cbRepeat")) {
            currentJob.updateChanges(ATTR_RUN_MODE, String.format("set %s = %s", ATTR_RUN_MODE, getRunValue(cbRepeat.getValue().toString())));
        }
        btnUpdate.setDisable(false);
        cbWatchJob.setDisable(true);
    }

}
