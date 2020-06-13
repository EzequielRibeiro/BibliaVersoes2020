package com.projeto.biblianvi.BibliaVersoes;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BibliaBancoDadosHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private Cursor cursor;
    private SQLiteDatabase myDataBase;
    private String DB_PATH;
    private Context myContext;

    public BibliaBancoDadosHelper(Context context) {
        super(context, MainActivity.DATABASENAME, null, DATABASE_VERSION);

        SharedPreferences sharedPreferences = context.getSharedPreferences("DataBase",Context.MODE_PRIVATE);
        DB_PATH = sharedPreferences.getString("dataBasePatch"," ");
        myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */

    // Books table name
    private static final String TABELA_VERSES = "verse";


  /*
    public void addBook(Biblia book){
        Log.d("addBook", book.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = icon_new ContentValues();
        values.put(KEY_TITLE, book.getTitle()); // get title
        values.put(KEY_AUTHOR, book.getAuthor()); // get author

        // 3. insert
        db.insert(TABLE_BOOKS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }
   */
    private void openDataBase() {

        try {
            myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            //  SQLiteDatabase db = this.getWritableDatabase();
            //  myDataBase = this.getReadableDatabase();
            Log.e("Banco aberto", Boolean.toString(myDataBase.isOpen()));
            Log.e("Banco patch", myDataBase.getPath());

            StringBuilder sb = new StringBuilder();
            DatabaseUtils.dumpCursor(myDataBase.rawQuery("PRAGMA integrity_check", null), sb);
            Log.e("DATABASEIntegrity: ", sb.toString());

        }catch (NonFatalError e){

            File file = new File(DB_PATH);
            file.delete();

        }

    }

    public Biblia getSumVersosReadByBooks(int id) {

        Biblia b = new Biblia();
        String query = "select book.[id], book.[name] ,count(verse.[text]), sum (verse.[lido]) from  verse " +
                "inner join book on verse.[book_id] = book.[id] where book.[id] = " + Integer.toString(id) + ";";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);
        cursor.moveToFirst();

        b.setId(cursor.getInt(0));
        b.setBooksName(cursor.getString(1));
        b.setTotalDeVersiculos(cursor.getInt(2));
        b.setTotalDeVersosLidos(cursor.getInt(3));

        return b;
    }

    public List<Biblia> getSumAllVersosReadByTestament(int id) {

        List<Biblia> versosLidos = new LinkedList<Biblia>();

        // 1. build the query
        String query = "select book.[id], book.[name] ,count(verse.[text]), sum(verse.[lido]) from verse " +
                "inner join book on verse.[book_id] = book.[id] where book.[testament_reference_id] = "+id+
                " group by book.[name] " +
                "ORDER BY book.[id];";

        // 2. get reference to writable DB
        //SQLiteDatabase db = this.getWritableDatabase();

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia b;

        if (cursor.moveToFirst()) {
            do {
                b = new Biblia();
                b.setId(cursor.getInt(0));
                b.setBooksName(cursor.getString(1));
                b.setTotalDeVersiculos(cursor.getInt(2));
                b.setTotalDeVersosLidos(cursor.getInt(3));
                versosLidos.add(b);
            } while (cursor.moveToNext());
        }

        close();
        // return books
        return versosLidos;
    }

    public List<Biblia> getBook(String book) {

        List<Biblia> books = new LinkedList<Biblia>();

       /*
               String query = "select books.id,testament.name,books.name,verses.chapter,verses.verse,verses.text,verses.lido,verses.rowid " +
                "from testament,verses,books where testament.id = " +
                "verses.testament and books.id = verses.book and books.name like '" + book + "%';";
       */

        String query = "select book.id,testament.name,book.name,verse.chapter,verse.verse,verse.text,verse.lido,verse.rowid, stories.title " +
                "from testament,verse,book left join stories ON stories.book + 1 = book.id and stories.chapter = verse.chapter and " +
                "stories.verse = verse.verse where book.id = verse.book_id and book.name like '" + book + "';";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if (cursor.moveToFirst()) {
            do {
                biblia = new Biblia();
                biblia.setIdBook(cursor.getInt(0));
                biblia.setTestamentName(cursor.getString(1));
                biblia.setBooksName(cursor.getString(2));
                biblia.setChapter(cursor.getString(3));
                biblia.setVerseNum(cursor.getString(4));
                biblia.setText(cursor.getString(5));
                biblia.setLido(cursor.getInt(6));
                biblia.setIdVerse(cursor.getString(7));
                biblia.setTitleCapitulo(cursor.getString(8));
                if (cursor.getString(8) != null)
                    Log.e("title", cursor.getString(8));

                // Add book to books
                books.add(biblia);
            } while (cursor.moveToNext());
        }

        close();
        // return books
        return books;
    }


    // Get All Books
    public List<Biblia> getAllBooksName() {

        List<Biblia> books = new LinkedList<Biblia>();

        // 1. build the query
        String query = "select book.[name],book.[testament_reference_id] from book;";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        Biblia biblia;
        if (cursor.moveToFirst()) {
            do {
                biblia = new Biblia();
                biblia.setBooksName(cursor.getString(0));
                biblia.setTestamentName(cursor.getString(1));
                books.add(biblia);
            } while (cursor.moveToNext());
        }
        close();
          return books;
    }

    public void limparVersLidos(){

        int row;
        String query = "UPDATE "+TABELA_VERSES+" set lido = 0 where lido = 1";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        ContentValues vl = new ContentValues();
        vl.put("lido", "0");

        try {
            db.beginTransaction();
            db.execSQL(query);
          //  row = db.update("verses", vl, "id = ?", icon_new String[]{});
            db.setTransactionSuccessful();

        } catch (Exception e) {
            throw  new Error("Update");

        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public int getQuantidadeVersos(String livroEpistola, String capitulo) {

        int i = 0;
        String query = "select COUNT(verse.verse) from verse,book where book.id = verse.book_id " +
                "and book.name like '"+livroEpistola+"%' and verse.chapter = "+capitulo;

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst())
            i = cursor.getInt(0);

        close();

        return i;
    }

    public int getQuantidadeCapitulos(String livroEpistola) {

        int i = 0;
        String query ="select COUNT(DISTINCT verse.chapter) from verse,book where book.id = verse.book_id and book.name like '"+livroEpistola+"%'";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst())
            i = cursor.getInt(0);

        close();

        return i;
    }

    public int setLidoVerso(Biblia bi){

        int row = 0;
        String query = "UPDATE "+TABELA_VERSES+" set lido = 1 where verse.[rowid] = "+bi.getIdVerse()+";";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        // SQLiteDatabase db = this.getWritableDatabase();

        ContentValues vl = new ContentValues();
        vl.put("lido", "1");

        try {

            db.beginTransaction();

            row = db.update("verse", vl, "rowid = ?", new String[]{String.valueOf(bi.getIdVerse())});

            db.setTransactionSuccessful();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) { // everything else
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        return row;

    }

    public int getQuantCompartilhar() {

        int i = 0;
        String query = "select count(msg) from compartilhar";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

            if (cursor.moveToFirst())
                i = cursor.getInt(0);


        close();

        return i;
    }

    public void setVersLimparCompartilhar(){

        String query = "delete from compartilhar";

        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        try {

            db.beginTransaction();

            db.execSQL(query);

            db.setTransactionSuccessful();


        } catch (Exception e) {

            throw  new Error("Delete compartilhar");

        } finally {
            db.endTransaction();
            db.close();
        }


    }

    public void setVersCompartilhar(Biblia bi){

        String versiculo = bi.getVersesText() + " (" + bi.getBooksName() + " " + bi.getChapter() + ":" + bi.getVersesNum() + ") ";
        String query = "insert into compartilhar (msg) values ('"+versiculo+"')";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

            try {
                db.beginTransaction();
                db.execSQL(query);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                throw new Error("Insert versiculo");
            } finally {
                db.endTransaction();
                db.close();
            }
    }

    public StringBuffer getVersCompartilhar() {

       StringBuffer stringBuffer = new StringBuffer();

        String query = "select msg from compartilhar";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);


        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                    stringBuffer.append(cursor.getString(0).concat(" \n"));


                } while (cursor.moveToNext());
            }


        close();

        return stringBuffer;
    }

    public Dicionario getDicionarioTexto(String id){

        String query = "select id,palavra,texto from [dicionario] where id = "+id+"";

        openDataBase();

        Dicionario dic = null ;

        cursor = myDataBase.rawQuery(query, null);

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {

                    dic = new Dicionario();
                    dic.setId(cursor.getInt(0));
                    dic.setPalavra(cursor.getString(1));
                    dic.setTexto(cursor.getString(2));

            }


        close();

        return dic;

    }

    public ArrayList<Dicionario> getDicionarioPalavra(){

        String query = "select id, palavra from [dicionario]";

        ArrayList<Dicionario> listDic = new ArrayList<Dicionario>();

        openDataBase();

        Dicionario dic ;

        cursor = myDataBase.rawQuery(query, null);

        if(cursor!=null && cursor.getCount()>0)
        if (cursor.moveToFirst()) {

            do {
                dic = new Dicionario();
                dic.setId(cursor.getInt(0));
                dic.setPalavra(cursor.getString(1));

                listDic.add(dic);
            }while(cursor.moveToNext());
        }


        close();

        return listDic;

    }

    public List<Biblia> pesquisarBiblia(String termo) {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select testament.name,book.name,verse.chapter,verse.[verse],verse.[text],verse.[lido],verse.[rowid] " +
                "from testament,verse,book where book.[id] = verse.[book_id] and verse.[text] like '%" + termo + "%';";


        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
           if (cursor.moveToFirst()) {
            do {
                //contains: case sensitive - filtra por caixa alto-baixa
                if(cursor.getString(4).contains(termo)) {
                    biblia = new Biblia();
                    biblia.setTestamentName(cursor.getString(0));
                    biblia.setBooksName(cursor.getString(1));
                    biblia.setChapter(cursor.getString(2));
                    biblia.setVerseNum(cursor.getString(3));
                    biblia.setText(cursor.getString(4));
                    biblia.setLido(cursor.getInt(5));
                    biblia.setIdVerse(cursor.getString(6));

                    // Add book to books
                    books.add(biblia);
                }

            } while (cursor.moveToNext());
        }


        close();
        // return books
        return books;
    }

    public List<Biblia> pesquisarBibliaTestamento(String termo,String testamento) {

        List<Biblia> books = new LinkedList<Biblia>();
        String args ="";

        if(testamento.equals("1")){
        args = "verse.book_id >= 1 and verse.book_id <= 39;";
        }else{
        args = "verse.book_id >= 40 and verse.book_id <= 66;";
        }

        String query = "select testament.name,book.name,verse.chapter,verse.verse,verse.text,verse.lido,verse.[rowid] " +
                "from testament,verse,book where book.[id] = verse.[book_id] and verse.[text] like '%"+termo+"%' "+
                "and "+args;

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                    //contains: case sensitive - filtra por caixa alto-baixa
                    if(cursor.getString(4).contains(termo)) {
                        biblia = new Biblia();
                        biblia.setTestamentName(cursor.getString(0));
                        biblia.setBooksName(cursor.getString(1));
                        biblia.setChapter(cursor.getString(2));
                        biblia.setVerseNum(cursor.getString(3));
                        biblia.setText(cursor.getString(4));
                        biblia.setLido(cursor.getInt(5));
                        biblia.setIdVerse(cursor.getString(6));

                        // Add book to books
                        books.add(biblia);
                    }


                } while (cursor.moveToNext());
            }


        close();
        // return books
        return books;
    }

    public List<Biblia> pesquisarBibliaLivro(String livro, String termo) {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select testament.name,book.name,verse.chapter,verse.verse,verse.text,verse.lido,verse.rowid " +
                "from testament,verse,book where book.id = verse.book_id and verse.text like '%"+termo+"%' "+
                "and book.[name] == '"+livro+"'";


        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                    //contains: case sensitive - filtra por caixa alto-baixa
                  if(cursor.getString(4).contains(termo)) {
                      biblia = new Biblia();
                      biblia.setTestamentName(cursor.getString(0));
                      biblia.setBooksName(cursor.getString(1));
                      biblia.setChapter(cursor.getString(2));
                      biblia.setVerseNum(cursor.getString(3));
                      biblia.setText(cursor.getString(4));
                      biblia.setLido(cursor.getInt(5));
                      biblia.setIdVerse(cursor.getString(6));

                      // Add book to books
                      books.add(biblia);

                  }

                } while (cursor.moveToNext());
            }


        close();
        // return books
        return books;
    }

    public int getQuantVersosLidosTotal(){

        int i = 0;

      String query = "select count(verse.[lido]) as total from verse  where verse.[lido] = '1';";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);

        if(cursor!=null && cursor.getCount()>0) {

            cursor.moveToFirst();

            i = cursor.getInt(0);

            close();


        }
        return i;
    }


    public String getBibleVersion() {


        String query = "select value from metadata where metadata.[key] == 'copyright';";

        openDataBase();
        cursor = myDataBase.rawQuery(query, null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(0);

        } else {
            return " ";
        }

    }

    public synchronized void close() {
        super.close();

        if(myDataBase != null)
            myDataBase = null;

        if(cursor != null){
            cursor.close();
            cursor = null;}

    }

    private int versDoDiaId(int max) {

        Random rand = new Random();
        int min = 1;

        if(max < 1)
          max = 1;

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void versDoDiaText(){

        VersDoDia v;

            v =  getVersDoDia();

            SharedPreferences settings = myContext.getSharedPreferences("versDiaPreference", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("assunto", v.getAssunto());
            editor.putString("versDia", v.getVersesText());
            editor.putString("livroNome",v.getBooksName());
            editor.putString("capVersDia", v.getChapter());
            editor.putString("verVersDia",v.getVersesNum());

            editor.commit();




    }

    public VersDoDia getVersDoDia() {


        int id = versDoDiaId(getQuantVersDoDia());

        String query = "select book.[name],verse.[chapter],verse.[verse],verse.[text],selecionados.[assunto]" +
                " from verse,book,selecionados where [verse].[book_id] = book.[id]" +
                " and [selecionados].[livro] = book.[id]" +
                " and [selecionados].[cap] = verse.[chapter]" +
                " and [selecionados].[vers] = verse.[verse]" +
                " and [selecionados].[id] ="+id;

        VersDoDia v = new VersDoDia();

        openDataBase();


            cursor = myDataBase.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            v.setBooksName(cursor.getString(0));
            v.setChapter(Integer.toString(cursor.getInt(1)));
            v.setVerseNum(Integer.toString(cursor.getInt(2)));
            v.setText(cursor.getString(3));
            v.setAssunto(cursor.getString(4));
        }


        close();

        return v;

    }

    private int getQuantVersDoDia() {

        int i = 0;
        String query ="select count(id) from selecionados";

        openDataBase();

            cursor = myDataBase.rawQuery(query, null);

            if (cursor.moveToFirst())
                i = cursor.getInt(0);



        close();

        Log.e("Quantidade",Integer.toString(i));

        return i;
    }

    public void deleteNota(String id){

        String query = "delete from nota where nota.id ="+ id;

        openDataBase();

        myDataBase.execSQL(query);

        close();

    }

    public void salvarNota(String titulo,String texto,String data){

        String query = "insert into nota (id,titulo,texto,data_) values (null,'"+titulo+"','"+texto+"','"+data+"')";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        try {
            db.beginTransaction();
            db.execSQL(query);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            throw  new Error("Insert nota");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<Anotacao> getNota() {

        ArrayList<Anotacao> notas = new ArrayList<Anotacao>();


        String query = "SELECT nota.[id],nota.[titulo],nota.[texto]," +
                "nota.[data_] FROM nota";

        openDataBase();
            cursor = myDataBase.rawQuery(query, null);
            Anotacao anotacao;
            if (cursor.moveToFirst()) {
                do {
                    anotacao = new Anotacao();
                    anotacao.setId(cursor.getInt(0));
                    anotacao.setTitulo(cursor.getString(1));
                    anotacao.setTexto(cursor.getString(2));
                    anotacao.setData(cursor.getString(3));
                    notas.add(anotacao);
                } while (cursor.moveToNext());
            }
        close();
        return notas;
    }

    public void deleteFavorito(String id){

        String query = "delete from favorito where idVerso = "+id;
        SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

        try {
            db.beginTransaction();
            db.execSQL(query);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw  new Error("Delete compartilhar");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void setFavorito(String i){


        String q = "insert into favorito (idVerso) values ('"+i+"')";
        String k = "select count(*) from favorito where idVerso ="+i;
        openDataBase();
        cursor = myDataBase.rawQuery(k,null);
        int c = 0;

        if(cursor.moveToFirst())
           c = cursor.getInt(0);

        close();
       if(c == 0) {

           SQLiteDatabase db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
           try {
               db.beginTransaction();
               db.execSQL(q);
               db.setTransactionSuccessful();
           } catch (Exception e) {
               throw new Error("Insert favorito ID");
           } finally {
               db.endTransaction();
               db.close();
           }
       }
    }

    public List<Biblia> getFavorito() {

        List<Biblia> books = new LinkedList<Biblia>();

        String query = "select favorito.idVerso,book.name,verse.chapter,verse.verse,verse.text " +
                "from verse,book,favorito where book.id = verse.book_id " +
                "and [favorito].[idVerso] = verse.[rowid]";

        openDataBase();

        cursor = myDataBase.rawQuery(query, null);

        Biblia biblia;

        if(cursor!=null && cursor.getCount()>0)
            if (cursor.moveToFirst()) {
                do {

                        biblia = new Biblia();
                        biblia.setIdVerse(cursor.getString(0));
                        biblia.setBooksName(cursor.getString(1));
                    biblia.setChapter(cursor.getString(2));
                        biblia.setVerseNum(cursor.getString(3));
                        biblia.setText(cursor.getString(4));

                    books.add(biblia);

                } while (cursor.moveToNext());
            }

        close();

        return books;
    }

    // tabelaName = favorito;
    //campos = "(id integer primary key,idVerso TINYINT(3) not null)";

    public void criarTabela(String tabelaName,String campos){

        String b = "CREATE TABLE IF NOT EXISTS "+tabelaName+campos;

        openDataBase();

        if(myDataBase.isOpen()) {
            myDataBase.execSQL(b);
            close();
        }

    }

    public int tabelaExiste(String tableName){


        String b = "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='"+tableName+"'";

         openDataBase();

          if(myDataBase.isOpen()) {

            cursor = myDataBase.rawQuery(b, null);

            cursor.moveToFirst();

            int i = cursor.getInt(0);

            close();

            return  i;

        }

        return  0;

    }


    public class Dicionario {

        private int id;
        private String palavra;

        private String texto;

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }



        public String getPalavra() {
            return palavra;
        }

        public void setPalavra(String palavra) {
            this.palavra = palavra;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }


    }

    public final class NonFatalError extends RuntimeException {

   public NonFatalError(String msg) {
            super(msg);
        }
    }
    public class VersDoDia extends Biblia {

        private String assunto = " ";

        public void setAssunto(String assunto){
            this.assunto  = assunto;

        }

        public String getAssunto(){
            return assunto;
        }

        @Override
        public String toString() {
            return getVersesText() + " (" + getBooksName() + " " + getChapter() + ":" + getVersesNum() + ")";
        }
    }


}