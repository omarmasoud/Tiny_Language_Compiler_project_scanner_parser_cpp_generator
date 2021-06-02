package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {
    static String code="";
    static ObservableList<MyToken> Tokens= FXCollections.observableArrayList();
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setResizable(false);
        Process process = Runtime.getRuntime().exec(
                "cmd /c a.exe",
                null,
                new File("C:\\Users\\omark\\Desktop"));
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
                "-fx-highlight-fill: #1FAA09; -fx-highlight-text-fill: red; -fx-text-fill: #0BF7FE;");
        CodeTA.setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.DELETE)
            {
               CodeTA.clear();
            }

        });

        CodeTA.setMinSize(1080,720);
        CodeTA.setEditable(true);
        TableView<MyToken> TokensTable=new TableView<MyToken>();
        TableColumn<MyToken,TokenType> TokenType=new TableColumn<>("Token Type");
        TokenType.setCellValueFactory(new PropertyValueFactory<MyToken,TokenType>("type"));
        TableColumn <MyToken,String>TokenValue=new TableColumn<>("Token Value");
        TokenValue.setCellValueFactory(new PropertyValueFactory<MyToken,String>("value"));
        TokensTable.getColumns().addAll(TokenType,TokenValue);

        CodeTextField_and_Tokens_Table.getChildren().addAll(CodeTA,TokensTable);
        Button GetTokensBt=new Button("Get tokens");
        GetTokensBt.setOnAction(e->{
            code=CodeTA.getText();
//            System.out.println(code);
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


            //todo table of tokentype _> token value
            //todo call mysccanner.Tokens

        });
        ItemHolder.getChildren().addAll(CodeTextField_and_Tokens_Table,GetTokensBt);
        primaryStage.setScene(new Scene(ItemHolder));
        primaryStage.show();

       // myscanner.NextToken().PrintToken();
       // Token mytok=new Token(TokenType.NUM,"1223");

    }



    public static void main(String[] args) {

        launch(args);
    }
}
