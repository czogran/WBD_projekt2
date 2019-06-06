package pracownik;

import Zadanie.Zadanie;
import Zadanie.ZadanieDAO;
import adres.Adres;
import adres.AdresDAO;
import antykwariat.Antykwariat;
import connection.DatabaseConnect;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import klient.Klient;
import klient.KlientDAO;
import oracle.sql.DATE;

import java.awt.event.KeyListener;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Pattern;

public class PracownikController {

    @FXML
    private Tab tabZadania;

    @FXML
    private Tab tabKlienci;

    @FXML
    private Pane paneClientButtons;

    @FXML
    Pane paneClientAdd;


    @FXML
    private TableView<Klient> tableKlienci;

    @FXML
    private TableView<Zadanie> tableZadania;

    @FXML
    private TableColumn<Klient, Integer> columnId;
    @FXML
    private TableColumn<Klient, String> columnImie;
    @FXML
    private TableColumn<Klient, String> columnNazwisko;
    @FXML
    private TableColumn<Klient, String> columnTelefon;
    @FXML
    private TableColumn<Klient, String> columnEmail;
    @FXML
    private TableColumn<Klient, String> columnAdres;
    @FXML
    private TableColumn<Klient, String> columnZarejestrowany;
    @FXML
    private TableColumn<Klient, String> columnDataRejestracji;


    @FXML
    private TableColumn<Zadanie, Integer> columnIdZadania;
    @FXML
    private TableColumn<Zadanie, String> columnRodzaj;
    @FXML
    private TableColumn<Zadanie, String> columnDataNadania;
    @FXML
    private TableColumn<Zadanie, String> columnRozpoczecie;
    @FXML
    private TableColumn<Zadanie, String> columnZakonczenie;
    @FXML
    private TableColumn<Pracownik, Integer> columnIdAntykwariatu;
    @FXML
    private TableColumn<Pracownik, Integer> columnIdAdresu;


    @FXML
    private TextField textImie;
    @FXML
    private TextField textNazwisko;
    @FXML
    private TextField textEmail;
    @FXML

    private TextField textTelefon;

    @FXML
    private TextField textSearch;

    @FXML
    private DatePicker textDataRejestracji;
    @FXML
    private ComboBox<String> textAdres;

    @FXML
    RadioButton textCzyZarejestrwany;


    @FXML
    private Button buttonDodaj;
    @FXML
    private Button buttonZaktualizuj;
    @FXML
    private Button buttonUsun;

    @FXML
    private Button buttonZaakceptuj;
    @FXML
    private Button buttonAnuluj;
    @FXML
    private Button updateButton;


    /**
     * Metoda inicjalizująca kontroler
     */
    @FXML
    private void initialize() {
        try {
            DatabaseConnect.ConnectToDatabase();


        } catch (SQLException ex) {
            ShowAlert(ex.toString());
        }
        DisplayClient();
    }

    private ObservableList<Antykwariat> antykwariaty = FXCollections.observableArrayList();
    private ObservableList<Adres> adresy = FXCollections.observableArrayList();
    private ObservableList<Zadanie> zadania = FXCollections.observableArrayList();
    private ObservableList<Klient> klienci = FXCollections.observableArrayList();

    enum Mode {
        UPDATE,
        INSERT;
    }

    Mode mode;

    public void DisplayZadania() {
        HideClient();
        zadania = new ZadanieDAO().GetAllZadania();

        columnIdZadania.setCellValueFactory(new PropertyValueFactory<Zadanie, Integer>("idZadania"));
        columnRodzaj.setCellValueFactory(new PropertyValueFactory<>("rodzajZadania"));
        columnDataNadania.setCellValueFactory(new PropertyValueFactory<>("dataNadania"));
        columnRozpoczecie.setCellValueFactory(new PropertyValueFactory<>("dataRozpoczecia"));
        columnZakonczenie.setCellValueFactory(new PropertyValueFactory<>("dataZakonczenia"));


        tableZadania.setItems(zadania);
    }

    private void ClearCells()
    {
        textCzyZarejestrwany.setSelected(false);
        textDataRejestracji.setValue(null);
        textAdres.getSelectionModel().clearSelection();
        textImie.clear();
        textNazwisko.clear();
        textTelefon.clear();
        textEmail.clear();

    }

    public void AnulujPressed()
    {
        ClearCells();
        ChangePaneAddClientActivity(true);
    }

    public void DisplayClient() {
        ShowClient();
//columnId.setText("aaaaa");
        klienci = new KlientDAO().GetAllKlienci();

        columnId.setCellValueFactory(new PropertyValueFactory<Klient, Integer>("id_klienta"));
        columnImie.setCellValueFactory(new PropertyValueFactory<Klient, String>("imie"));
        columnNazwisko.setCellValueFactory(new PropertyValueFactory<Klient, String>("nazwisko"));
        columnTelefon.setCellValueFactory(new PropertyValueFactory<Klient, String>("nr_telefonu"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<Klient, String>("email"));
        columnZarejestrowany.setCellValueFactory(new PropertyValueFactory<Klient, String>("czy_zarejestrowany"));
        columnDataRejestracji.setCellValueFactory(new PropertyValueFactory<Klient, String>("data_rejestracji"));
        columnAdres.setCellValueFactory(new PropertyValueFactory<Klient, String>("id_adresu"));
       // System.out.println(klienci.get(0).getId_adresu());
        tableKlienci.setItems(klienci);
    }

    public void ActivateAddClient() {
        ClearCells();
        mode = Mode.INSERT;
        ChangePaneAddClientActivity(false);
        SetComboBoxes();
    }

    public void ActivateUpdateClient() {
       ClearCells();
        mode = Mode.UPDATE;
        ChangePaneAddClientActivity(false);


        Klient klient;
        try {

            klient = tableKlienci.getSelectionModel().getSelectedItem();
            idKlienta=klient.getId_klienta();

            FillAddPane(klient);
            DisplayClient();
        } catch (Exception ex) {
            if (ex.equals("NullPointerException")) ;
            InformationAlert("WYBIERZ KLIENTA DO ZAKTUALIZOWANIA");
        }
        SetComboBoxes();
    }


    private String imie;
    private String nazwisko;
    private String numerTelefonu;
    private String email;
    private String czyZarejestrowany;
    private Date dataRejestracji;
    private Integer idAdresu;
    private String information;
    private int idKlienta;

    public void AddClient() throws Exception {
        boolean parametersOK = WalidataAddClient();

        if (mode == mode.INSERT) {
            if (!parametersOK) {
                Alert info = new Alert(Alert.AlertType.ERROR);
                info.setContentText(information);
                info.show();
            } else {
                idKlienta = new KlientDAO().MaxIdEntry();
                InformationAlert(idAdresu.toString());
                new KlientDAO().InsertKlient(idKlienta, imie, nazwisko, numerTelefonu, email, czyZarejestrowany, dataRejestracji, idAdresu);
            }
        } else if (mode == mode.UPDATE) {
            if (!parametersOK) {
                Alert info = new Alert(Alert.AlertType.ERROR);
                info.setContentText(information);
                info.show();
            } else {
                InformationAlert(idAdresu.toString());
                InformationAlert(idAdresu.toString());
                new KlientDAO().UpdateKlient(idKlienta, imie, nazwisko, numerTelefonu, email, czyZarejestrowany, dataRejestracji, idAdresu);
            }
        }


        DisplayClient();
    }


    private boolean WalidataAddClient() {
        imie = textImie.getText();
        nazwisko = textNazwisko.getText();
        information = "";
        numerTelefonu = textTelefon.getText();
        numerTelefonu = textTelefon.getText();

        LocalDate localDate = textDataRejestracji.getValue();
       // Date.valueOf(textUrodzenie.getValue());
        dataRejestracji = Date.valueOf(textDataRejestracji.getValue());
        if (textCzyZarejestrwany.isSelected())
            czyZarejestrowany = "t";
        else
            czyZarejestrowany = "n";


        email = textEmail.getText();
        idAdresu = -1;
        for (Adres adr : adresy) {
            if (adr.getMiasto().equals(textAdres.getSelectionModel().getSelectedItem())) {
                idAdresu = adr.getId_adresu();
                break;
            }
        }
        try {
            if (idAdresu.equals(-1)) {
                information = "wstaw adres";
                return false;
            }

            if (((!email.contains("@")) && (!email.equals("")))) {
                information = "email bez malpy";
                return false;
            }

            if (imie.equals("") || nazwisko.equals("")) {
                information = " nie podano danych pracownika";
                return false;
            }
            if (Pattern.matches(".*\\d.*", imie) || Pattern.matches(".*\\d.*", nazwisko)) //imie.matches(".*\\d.*")
            {
                information = " zly pattern";
                return false;
            }
            if (numerTelefonu.equals("")) {
                information = "nie podano numeru telefonu";
                return false;
            }
            if (imie.length() > 20 || nazwisko.length() > 20 || numerTelefonu.length() > 9) {
                information = " za dlugie dane";
                return false;
            }
            if (czyZarejestrowany.equals("t")) {
                try {
                    dataRejestracji = Date.valueOf(textDataRejestracji.getValue());
                } catch (Exception ex) {
                    Alert info = new Alert(Alert.AlertType.ERROR);
                    info.setContentText("podaj date rejestracji");
                    info.show();
                    return false;
                }
                dataRejestracji = Date.valueOf(textDataRejestracji.getValue());
            } else {
                dataRejestracji = null;
            }
        }
        catch (Exception ex)
        {

        }

        return true;
    }

    /**
     * function is used for button USUN
     * this function is responsible for removing client
     */
    public void DeleteClient() {
        int id;
        if (ConfirmationAlert("Czy chcesz usunąć klienta?")) {
            try {

                id = tableKlienci.getSelectionModel().getSelectedItem().getId_klienta();
                new KlientDAO().DeleteClient(id);
                DisplayClient();
            } catch (Exception ex) {
                if (ex.equals("NullPointerException")) ;
                InformationAlert("WYBIERZ KLIENTA DO USUNIĘCIA");
            }
        }

    }



    private void FillAddPane(Klient klient) {
        textEmail.setText(klient.getEmail());

        textImie.setText(klient.getImie());
        textTelefon.setText(klient.getNr_telefonu());
        textNazwisko.setText(klient.getNazwisko());
        System.out.println(klient.getId_adresu());
        for (Adres adres:adresy)
        {
            if(adres.getId_adresu().equals(klient.getId_adresu())) {
                textAdres.setValue(adres.getMiasto());
                break;
            }
        }



        if (klient.getCzy_zarejestrowany().equals("tak"))
             textCzyZarejestrwany.setSelected(true);
         else
        textCzyZarejestrwany.setSelected(false);

        // textCzyZarejestrwany.setText(klient.getCzy_zarejestrowany());
        textDataRejestracji.setValue(klient.getRegistrationData());

        //  buttonAnuluj.setDisable(status);
        // buttonZaakceptuj.setDisable(status);
    }

    /**
     * function is used for setting status of panel for client controls for adding new client
     *
     * @param status false- panel is active, true- panel is discative
     */
    private void ChangePaneAddClientActivity(boolean status) {
        textEmail.setDisable(status);

        textImie.setDisable(status);
        textTelefon.setDisable(status);
        textNazwisko.setDisable(status);
        textAdres.setDisable(status);

        textCzyZarejestrwany.setDisable(status);
        textDataRejestracji.setDisable(status);

        buttonAnuluj.setDisable(status);
        buttonZaakceptuj.setDisable(status);
    }


    public void HideClient() {
        paneClientAdd.setVisible(false);
        paneClientButtons.setVisible(false);
    }

    public void ShowClient() {
        paneClientAdd.setVisible(true);
        paneClientButtons.setVisible(true);
    }

    /**
     * Metoda wypełniająca komponenty Combobox
     * - adresami (miasto)
     * - antykwariatami (nazwa)
     */
    private void SetComboBoxes() {
        adresy = new AdresDAO().GetAllAdresy();

        antykwariaty.forEach(antykwariat -> textAdres.getItems().add(antykwariat.getNazwa()));
        adresy.forEach(adres -> textAdres.getItems().add(adres.getMiasto()));
    }


    /**
     * Function for displaying alerts
     *
     * @param ex
     */
    private void ShowAlert(Object ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(ex.toString());
        alert.show();
    }

    private void InformationAlert(String information) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(information);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.show();
    }

    /**
     * function is used to confirm user actions
     *
     * @param header question to user if he is sure of his action
     * @return confirms if action is going to be done or not
     */
    private boolean ConfirmationAlert(String header) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Czy jesteś pewien?");
        alert.setHeaderText(header);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        }
        return false;

    }
}