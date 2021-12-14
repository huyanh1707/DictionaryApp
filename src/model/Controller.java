package model;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ImageView btn_dict, btn_tran, btn_more, btn_find, btn_speak, btn_down, btn_speak_gg, btn_exit;
    @FXML
    private TextField wordField, inputWord_add, phoneticInput, inputWord_delete;
    @FXML
    private TextArea wordArea, wordDetail;
    @FXML
    private Button ggTranslate_en_vi, ggTranslate_vi_en, btn_add, btn_delete, btn_edit;
    @FXML
    private AnchorPane h_dict, h_more, h_tran;
    @FXML
    private Label meaningLabel, meaningLabel_gg, WordLabel, PhoneticsLabel;
    @FXML
    private ListView<String> recommendWordsList;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private Line line;
    @FXML
    private ScrollPane sp;

    Stage stage;
    Dictionary dictionary;
    Trie trie;
    List<String> wordList;
    ObservableList<String> observableList;
    VoiceManager vm;
    Voice voice;
    String filename = "src/data/dictionaries.txt";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dictionary = new Dictionary();
        dictionary.loadFromFile(filename);
        dictionary.sortWords();
        trie = new Trie();
        wordList = new ArrayList<>();
        vm = VoiceManager.getInstance();
        voice = vm.getVoice("kevin16");
        voice.allocate();
        btn_edit.getStyleClass().add("editbutton");

        for (Word word : dictionary.getWords()) {
            trie.insert(word.getWord());
            wordList.add(word.getWord());
        }

        wordField.setOnKeyReleased(keyEvent -> {
            textField_event();
            if(keyEvent.getCode() == KeyCode.ENTER){
                dictionarySearcher();
                if(recommendWordsList.isVisible()) {
                    recommendWordsList.setVisible(false);
                }
            }
        });

        ggTranslate_en_vi.setOnAction(event -> {
            try {
                useAPI_en_vi();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        ggTranslate_vi_en.setOnAction(event -> {
            try {
                useAPI_vi_en();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        observableList = FXCollections.observableList(wordList);
        recommendWordsList.setItems(observableList);

        recommendWordsList.setOnMouseClicked(click -> {
            String str = recommendWordsList.getSelectionModel().getSelectedItems().toString();
            str = str.substring(1, str.length() - 1);
            wordField.setText(str);
            if(click.getClickCount() == 2) {
                dictionarySearcher();
            }
        });
    }

    @FXML
    private void handleButtonAction(MouseEvent event) {
        if (event.getTarget() == btn_dict) {
            h_dict.setVisible(!h_dict.isVisible());
            h_tran.setVisible(false);
            h_more.setVisible(false);
        }
        if (event.getTarget() == btn_tran) {
            clearContent();
            h_dict.setVisible(false);
            h_tran.setVisible(!h_tran.isVisible());
            h_more.setVisible(false);
        }
        if (event.getTarget() == btn_more) {
            clearContent();
            h_dict.setVisible(false);
            h_tran.setVisible(false);
            h_more.setVisible(!h_more.isVisible());
        }
        if (event.getTarget() == btn_speak) {
            btn_speak_event();
        }
        if (event.getTarget() == btn_speak_gg) {
            btn_speak_gg_event();
        }
        if (event.getTarget() == btn_find) {
            dictionarySearcher();
        }
        if (event.getTarget() == btn_down) {
            recommendWordsList.setVisible(!recommendWordsList.isVisible());
        }
        if (event.getTarget() == btn_exit) {
            btn_exit_event();
        }
    }

    @FXML
    void handleAction(ActionEvent event) {
        if(event.getTarget() == btn_add) {
            addAWord();
        }
        else if (event.getTarget() == btn_delete) {
            deleteAWord();
        }
        else if (event.getTarget() == btn_edit) {
            editAWord();
        }
    }

    /**
     * Type the word to search and autocomplete.
     */
    private void textField_event() {
        if(!recommendWordsList.isVisible()) {
            recommendWordsList.setVisible(true);
        }
        String s = wordField.getText();
        List<String> a = trie.autoComplete(s);
        observableList.clear();
        observableList.addAll(a);
        recommendWordsList.setMaxHeight(observableList.size()*23.5);
    }

    /**
     * Search word event.
     */
    private void dictionarySearcher() {
        clearContent();
        line.setVisible(true);
        if(recommendWordsList.isVisible()) {
            recommendWordsList.setVisible(false);
        }
        String s = wordField.getText();
        if (!s.equals("")) {
            int result = dictionary.search(s);

            if (result >= 0) {
                WordLabel.setText(dictionary.getWords().get(result) + "\n");
                PhoneticsLabel.setText(dictionary.getWords().get(result).getPhonetics() + "\n");
                meaningLabel.setText(dictionary.getWords().get(result).getMeaning());
                sp.setContent(meaningLabel);
            } else {
                clearContent();
                WordLabel.setText("Not found! " + "\n" + "\n"
                        + "Maybe you wrote it wrong, see recommend list beside or use GoogleTransLate");
            }
        }
        else {
            clearContent();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("You have not entered the word, please type it");
            alert.show();
        }
    }

    /**
     * Clear the content.
     */
    private void clearContent() {
        line.setVisible(false);
        WordLabel.setText("");
        PhoneticsLabel.setText("");
        meaningLabel.setText("");
    }

    /**
     * Press Speak Button event.
     */
    private void btn_speak_event() {
        voice.speak(wordField.getText());
    }
    private void btn_speak_gg_event() {
        voice.speak(wordArea.getText());
    }

    /**
     * Press GoogleTranslate Button event.
     */
    private void useAPI_en_vi() throws IOException {
        String s = GoogleTranslate.translate("en", "vi", wordArea.getText());
        meaningLabel_gg.setText(s);
    }
    private void useAPI_vi_en() throws IOException {
        String s = GoogleTranslate.translate("vi", "en", wordArea.getText());
        meaningLabel_gg.setText(s);
    }

    /**
     * Press ADD button event.
     */
    private void addAWord() {
        String s_add = inputWord_add.getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add a Word");
        alert.setHeaderText("You're about add a word English to Vietnamese!");
        if(s_add.equals("")) {
            alert.setContentText("You have not entered the word, please type it");
            alert.show();
            return;
        }
        int result = Collections.binarySearch(dictionary.getWords(), new Word(s_add));
        if (result <= 0) {
            StringBuilder s2 = new StringBuilder(wordDetail.getText());
            String s3 = phoneticInput.getText();
            try {
                FileWriter fw = new FileWriter(filename,true);
                BufferedWriter bw = new BufferedWriter(fw);
                String ss = s3;
                if (s3.length() != 0) ss = "/" + ss + "/";

                bw.write("#"+ s_add + " " + ss + "\n" + s2 + "\n");
                bw.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            updateDictionary();
            alert.setContentText("The word \"" + s_add + "\" added!");
        }
        else {
            alert.setContentText("The word already exist!");
        }
        alert.show();
    }

    /**
     * Press EDIT button event.
     */
    private void editAWord() {
        String s_edit = inputWord_add.getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Edit a Word");
        alert.setHeaderText("You're about edit a word English to Vietnamese!");
        if (s_edit.equals("")) {
            alert.setContentText("You have not entered the word, please type it");
            alert.show();
            return;
        }
        int result = Collections.binarySearch(dictionary.getWords(), new Word(s_edit));
        if (result >= 0) {
            StringBuilder s3 = new StringBuilder(wordDetail.getText());
            String s2 = phoneticInput.getText();
            File oldFile = new File(filename);
            File tempFile = new File("src/data/temp.txt");
            FileWriter fw;
            BufferedWriter bw;
            try {
                fw = new FileWriter(tempFile, true);
                bw = new BufferedWriter(fw);
                for (Word w : dictionary.getWords()) {
                    if (!w.getWord().equals(s_edit)) {
                        bw.write("#" + w.getWord() + " " + w.getPhonetics() + "\n");
                        bw.write(w.getMeaning());
                    } else {
                        String ss = s2;
                        if (s2.length() != 0) ss = "/" + ss + "/";
                        bw.write("#" + s_edit + " " + ss + "\n" + s3 + "\n");
                    }
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            oldFile.delete();
            File newFile = new File(filename);
            tempFile.renameTo(newFile);
            updateDictionary();
            alert.setContentText("The word \"" + s_edit + "\" edited!");
        } else {
            alert.setContentText("This word is no longer in the dictionary!");
        }
        alert.show();
    }
    /**
     * Press DELETE button event.
     */
    private void deleteAWord() {
        String s_delete = inputWord_delete.getText();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete a word");
        alert.setHeaderText("You're about delete a word English to Vietnamese!");
        if(s_delete.equals("")) {
            alert.setContentText("You have not entered the word, please type it");
            alert.show();
            return;
        }
        int result = Collections.binarySearch(dictionary.getWords(), new Word(s_delete));
        if (result >=0) {
            File oldFile = new File(filename);
            File tempFile = new File("src/data/temp.txt");
            FileWriter fw;
            BufferedWriter bw;
            try {
                fw = new FileWriter(tempFile,true);
                bw = new BufferedWriter(fw);
                for (Word w : dictionary.getWords()) {
                    if (!w.getWord().equals(s_delete)) {
                        bw.write("#" + w.getWord() + " " + w.getPhonetics() + "\n");
                        bw.write(w.getMeaning());
                    }
                }
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            oldFile.delete();
            File newFile = new File(filename);
            tempFile.renameTo(newFile);
            updateDictionary();
            alert.setContentText("The word \"" + s_delete + "\" deleted!");
        }
        else {
            alert.setContentText("This word is no longer in the dictionary!");
        }
        alert.show();
    }

    /**
     * Press exit button event.
     */
    private void btn_exit_event() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about exit!");
        alert.setContentText("Don't you need to search anything else before exiting?");

        if(alert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) scenePane.getScene().getWindow();
            stage.close();
        }
    }

    private void updateDictionary() {
        dictionary.loadFromFile(filename);
        dictionary.sortWords();
        wordList = new ArrayList<>();
        trie = new Trie();
        for (Word word : dictionary.getWords()) {
            trie.insert(word.getWord());
            wordList.add(word.getWord());
        }
        observableList.clear();
        observableList.addAll(wordList);
        recommendWordsList.setItems(observableList);
        recommendWordsList.setMaxHeight(observableList.size()*24);
    }
}
