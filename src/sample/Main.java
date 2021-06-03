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
    static String code="";
    static ObservableList<MyToken> Tokens= FXCollections.observableArrayList();
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setResizable(false);
//        Process process = Runtime.getRuntime().exec(
//                "cmd /c a.exe",
//                null,
//                new File("C:\\Users\\omark\\Desktop"));
/*        Runtime.getRuntime().
                exec("cmd /c cd desktop:\\ \"\" gcc no.c");*/
        primaryStage.setTitle("My Tiny Compiler");




        VBox ItemHolder=new VBox();
        ItemHolder.setStyle("-fx-background-color:#262B29" +
                "");
        ItemHolder.setSpacing(15);
        ItemHolder.setPadding(new Insets(15));
        HBox CodeTextField_and_Tokens_Table=new HBox();
        TextArea CodeTA=new TextArea();

        CodeTA.setStyle("-fx-control-inner-background:#000000; " +
                "-fx-highlight-fill: #1FAA09; -fx-highlight-text-fill: red; -fx-text-fill: #0BF7FE; -fx-font-family: Arial");
        CodeTA.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.DELETE)
            {
               CodeTA.clear();
            }

        });

        CodeTA.setMinSize(1080,720);
        CodeTA.setEditable(true);

        TableView<InjectedList> TokensTable=new TableView<InjectedList>();
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
        Button GetTokensBt=new Button("Get tokens");
        GetTokensBt.setStyle("-fx-font-family: Arial");
        GetTokensBt.setOnAction(e->{
            code=CodeTA.getText();

           CompilerScanner myscanner=new CompilerScanner(code);
           CompilerParser myparser=new CompilerParser(myscanner);
            try {
                myparser.parse();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            myscanner.ResetTokenizer();
            while(myscanner.HasMoreTokens())
            {
            try {
            myscanner.NextToken();
            } catch (Exception exception) {
                //Todo msg box printing exception in red
            exception.printStackTrace();

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
        ItemHolder.getChildren().addAll(CodeTextField_and_Tokens_Table,GetTokensBt);
        primaryStage.setScene(new Scene(ItemHolder));
        primaryStage.show();



    }



    public static void main(String[] args) {

        launch(args);
    }
}
