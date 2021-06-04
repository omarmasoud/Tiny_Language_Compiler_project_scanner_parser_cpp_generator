package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    static CompilerScanner myscanner;
    CompilerParser myparser;
    static String code="";
    TextArea ParserTA=new TextArea();
    TextArea errorpane=new TextArea();
    static ObservableList<MyToken> Tokens= FXCollections.observableArrayList();
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setResizable(false);
        primaryStage.setTitle("My Tiny Compiler");
        VBox ItemHolder=new VBox();
        ItemHolder.setStyle("-fx-background-color:#262B29" + "");
        ItemHolder.setSpacing(15);
        HBox CodeTextField_and_Tokens_Table=new HBox();
        CodeTextField_and_Tokens_Table.setSpacing(10);
        TextArea CodeTA=new TextArea();

        HBox bottompane=new HBox();
        ParserTA.setMinSize(720,150);
        Button GetTokensBt=new Button("Get tokens");
        Button ParseButton =new Button("Start parsing");
        Button GenerateCPPbtn=new Button( " generate cpp");
        errorpane.setMinSize(360,150);
        errorpane.setStyle("-fx-control-inner-background:#4F4B4B; " +
                "-fx-highlight-fill: #1FAA09; -fx-highlight-text-fill: red; -fx-text-fill: #EF1912; -fx-font-family: Arial");
        errorpane.setEditable(false);
        errorpane.setText("this section is for errors only");
        VBox btns=new VBox();
        btns.setSpacing(30);
        btns.getChildren().addAll(GetTokensBt,ParseButton,GenerateCPPbtn);
        GenerateCPPbtn.setOnAction(e->{
            try {
                myparser.Generate_Cpp_Code();
            } catch (Exception exception) {
                String exp=exception.toString().replaceAll("java.lang.Exception: ","");
                errorpane.setText(exp);
            }
        });
        bottompane.getChildren().addAll(ParserTA,errorpane,btns);
        bottompane.setSpacing(40);
        CodeTA.setStyle("-fx-control-inner-background:#000000; " +
                "-fx-highlight-fill: #1FAA09; -fx-highlight-text-fill: red; -fx-text-fill: #0BF7FE; -fx-font-family: Arial");
        ParserTA.setEditable(false);
        ParserTA.setText("parsing statements are written here");
        ParserTA.setStyle("-fx-control-inner-background:#636866; " +
                "-fx-highlight-fill: #1FAA09; -fx-highlight-text-fill: red; -fx-text-fill: #44F903; -fx-font-family: Arial");
        CodeTA.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.DELETE)
            {
                CodeTA.clear();
            }

        });
        ParseButton.setOnAction(e->
        {
            code=CodeTA.getText();
            myscanner=new CompilerScanner(code);
            myparser=new CompilerParser(myscanner,ParserTA);

            try {
                myparser.parse();
            } catch (Exception exception) {
                String exp=exception.toString().replaceAll("java.lang.Exception: ","");
                errorpane.setText(exp);
            }
        });

        CodeTA.setMinSize(1080,600);
        CodeTA.setEditable(true);

        TableView<InjectedList> TokensTable=new TableView<InjectedList>();
        TokensTable.setMinSize(350,600);
        TokensTable.setStyle("-fx-font-family: Arial");

        TableColumn<InjectedList,String> TokenType=new TableColumn<>("Token Type");
        TokenType.prefWidthProperty().bind(TokensTable.widthProperty().multiply(0.5));

        TableColumn <InjectedList,String>TokenValue=new TableColumn<>("Token Value");
        TokenValue.prefWidthProperty().bind(TokensTable.widthProperty().multiply(0.5));
        TokensTable.getColumns().add(TokenValue);
        TokensTable.getColumns().add(TokenType);


        TokenType.setCellValueFactory(new PropertyValueFactory<>("tkType"));
        TokenValue.setCellValueFactory(new PropertyValueFactory<>("tkVal"));




        CodeTextField_and_Tokens_Table.getChildren().addAll(CodeTA,TokensTable);
        GetTokensBt.setStyle("-fx-font-family: Arial");
        GetTokensBt.setOnAction(e->{
            code=CodeTA.getText();
            myscanner=new CompilerScanner(code);
            myscanner.ResetTokenizer();
            while(myscanner.HasMoreTokens())
            {
                try {
                    myscanner.NextToken();
                } catch (Exception exception) {
                    String exp=exception.toString().replaceAll("java.lang.Exception: ","");
                   errorpane.setText(exp);
                    break;
                }
                //token.PrintToken();
            }


            ObservableList<InjectedList> observableList = FXCollections.observableArrayList();
            observableList.clear();


            for (int i = 0; i < myscanner.Tokens.size(); i++) {
                observableList.add(new InjectedList(myscanner.Tokens.get(i).getTokenvalue(),
                        myscanner.Tokens.get(i).getTokenType().toString()));
            }



            TokensTable.setItems(observableList);



            //todo table of tokentype _> token value
            //todo call mysccanner.Tokens

        });
        ItemHolder.getChildren().addAll(CodeTextField_and_Tokens_Table,bottompane);
        primaryStage.setScene(new Scene(ItemHolder));
        primaryStage.show();



    }



    public static void main(String[] args) {

        launch(args);
    }
}