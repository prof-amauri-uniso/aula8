package com.uniso.lpdm.cafeteria_cap7;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*ARMAZENAR DADOS
*
* A maioria dos apps precisa persistir dados. Normalmente isso é feito com um banco de dados, e no
* caso do android, o mais comum é o uso do SQLite. Alguns dos motivos para a escolha dele são:
*
*   - Ele é leve, quando não estamos utilizando, ele não ocupa nenhum tempo de processador. Em um
* dispositivo móvel, essa vantagem é importante para ecnomizar bateria
*
*   - É otimizado para atender um único usuário, não é necessário passar por um processo de autenticação
*
*   - É estável e rápido
*
* O banco de dados no dispositivo fica armazenado no diretorio /data/data/nome do pacote/databeses
* mas não fica visivel os usuários.*/


/*Para interagirmos com o SQLite precisamos utilizar uma classe do android chamada de SQLiteOpenHelper.
* A forma de fazermos isso é criarmos a nossa própria classe (que aqui está sendo chamada de DatabaseHelper)
* e fazer com que ela extenda a classe do android SQLiteHelper
*
* Com a classe que extende SQLiteOpenHelper podemos controlar a criação e atualização do banco de dados,
* popular com os dados inicias para o app, controlar a estrutura do banco para novas versões do app, fazendo
* assim upgrades e downgrades. Sendo assim, as responsabilidades dessa classe são:
*    - Criação do banco de dados
*    - Mediação do acesso ao banco de dados
*    - Manutenção do banco de dados
*
*
* Uma vez criada a classe que extende SQLiteOpenHelper, somos obrigados a implementar dois métodos/funções
*    - onCreate - chamado quando o banco de dados é criado pela primeira vez no dispositivo
*    - onUpgrade - chamado quando o banco precisa ser atualizado*/
public class DatabaseHelper extends SQLiteOpenHelper {

    /*o SQLiteOpenHelper precisa de duas informações para desenpenhar suas funçãoes: o nome do banco
    * de dados e a versão do mesmo. Colocamos na forma de constantes para padronizar o nome e
    * as referencias em toda a aplicação*/
    private static final String DB_NAME = "cafeteria";
    private static final int DB_VERSION = 2;

    /*Aqui temos o construtor da classe, uam função executada quando ela é criada na memória.
    * É aqui que ocorrer a criação do arquivo do banco de dados no dispositivo. Quem passa o contexto
    * é o proprio android. O Contexto é a forma que o android disponibiliza funcionalidades para a aplicação,
    * como a possibilidade de trabalhar com itenções por exemplo. */
    DatabaseHelper(Context context){

        /*Na verdade chamamos o construtur da classe pai, ou seja, o contrutor de SQLIteOpenHelper,
        * ele é que efetivamente faz a criação do banco de dados*/
        super(context, DB_NAME, null, DB_VERSION);

    }

    /*O SQLiteOpenHelper tem a função de criar o banco de dados SQLite. Quando utilizado pela primeira
    * vez no dispositivo, um banco de dados vazio é criado, em seguida, o método/função onCreate é
    * chamado. O parametro SQLiteDatabase representa o banco que foi criado
    *
    * Essencialmente o trabalho realizado no onCreate é a criação da estrutura de tabelas do banco de
    * dados*/
    @Override
    public void onCreate(SQLiteDatabase db) {

        /*Para facilitar o reuso de código da criação e do upgrade, colocamos o código que cria
        * a tabela do exemplo no método atualizarBanco
        *
        * aqui passamos a versão "velha" como zero, assim na verificação do método, toda a estrutura
        * inicial é criada*/
        atualizarBanco(db, 0, DB_VERSION);

    }

    /*Quando se altera um banco de dados, existem dois cenários que devem ser tratados pelo programador
    * 1 - O usuário nunca instalou o app e seu dispositivo não contém o banco de dados.
    * 2 - O usuário instala uma nova versão do app que contém outra versão do banco de dados
    *
    * Para verificar se o banco de dados deve ser atualizado, o SQLiteOpenHelper verifica o numero
    * de versão. Quando o usuário instala a última versão do app no seu dispositivo, na primeira vez
    * que o app usa o banco de dados, o SQLiteOpenHelper verifica seu número de versão em comparação
    * com o do banco de dados no dispositivo. Caso o valor no dispositivo seja menor, o método/função
    *  onUpgrade é chamado. Caso seja maior, o método/função onDownGrade que entra em ação. Usamos o
    * numero das versões para saber que alterações devem ocorrer no banco de dados que está no
    * dispositivo*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*Para facilitar o reuso de código da criação e do upgrade, colocamos o código que cria
         * a tabela do exemplo no método atualizarBanco*/
        atualizarBanco(db, oldVersion, newVersion);

    }

    /*Para simplificar a ação de inserir várias linhas para uma mesma tabela/banco, criamos esse
    * método/função auxiliar*/
    public static void insertBebida(SQLiteDatabase db, String nome, String descricao,
                                    int imagem_resource_id){
        /*Para inserir dados em um banco de dados SQLite, primeiro especifique os valores que serão inseridos
        * na tabela. Para isso usamos um objeto/variavel do tipo ContentValues. Ele descreve um conjunto
        * de dados. Para cada registro que queremos criar, precisamos de um objeto/variavel do tipo
        * ContentValues*/
        ContentValues bebida = new ContentValues();
        /*Para adicionarmos os dados no ContentValues utilizamos o método/função de put, onde temos
        * um par de informações. A primeira é o nome do campo na tabela, a segunda é o dado que será
        * armazenado.*/
        bebida.put("nome", nome);
        bebida.put("descricao", descricao);
        bebida.put("imagem_resource_id", imagem_resource_id);
        /*Depois de adicionarmos todas as informações, usamos o método/função insert do banco de dados para
        * inserir informaçõs. Para isso, passamos a tabela, o valor null, e o objeto/variavel do tipo
        * ContentValues. O parametro nulo é usado caso passemos um objeto ContentValues vazio.*/
        db.insert("BEBIDA", null, bebida);
    }

    private void atualizarBanco(SQLiteDatabase db, int oldVersion, int newVersion){
        /*Variavel string para armazenar o código SQL que será executado*/
        String sql;

        /*Caso esteja sendo chamado no onCreate (parametro oldVersion = 0), esse método que cria
        * a estrutura básica é chamado*/
        if(oldVersion < 1){
            /*Na maioria das vezes devemos colocar um campo _id para nossas tabelas. É uma convenção
            * do android. O código do android foi desenvolvido esperando por um campo com esse nome
            * especifico para ser a chave primária
            *
            * Diferente da maioria dos sistemas de banco de dados, não precisa ser especificado o tamanho
            * da coluna no SQLite
            *
            * Usamos o comando CREATE TABLE  para criar uma tabela no banco de dados, a opção
            * PRIMARY KEY defini que aquele campo vai ser o identificador para todos os registros
            * da tabela, e portanto dois registros não poderão ter valores iguais para esse campo.
            * A opção AUTOINCREMENT informa que esse campo terá o valor gerado automaticamente
            * pelo banco, de forma sequencial*/

            sql = "CREATE TABLE BEBIDA (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT, " +
                    "descricao TEXT, " +
                    "imagem_resource_id INTEGER" +
                    ");";

            /*O método/função que executa comandos SQL no banco de dados é o execSQL*/
            db.execSQL(sql);

            /*Como o banco de dados está sendo criado do zero, precisamos popular com os dados iniciais.
            * precisamos usar o insert, mas ele está seprado no método/função insertBebida*/
            insertBebida(db, "Latte", "Um cafe com leite", R.drawable.latte);
            insertBebida(db, "Cappuccino", "Um Cappuccino", R.drawable.cappuccino);
            insertBebida(db, "Filtrado", "Um cafe filtrado", R.drawable.filtrado);
        }

        /*Caso o valor de oldVersion seja maior que zero, então significa que está sendo chamado uma
        * atualização, um upgrade ou um downgrade, nesse caso, é necessário avaliar a versão que o
        * usuário possui. Nesse exemplo, quando chamado pelo onUpgrade, o usuário já deve possuir a
        * versão 1 do banco, nesse caso, a alteração adicionando a coluna ocorre. Como são if's
        * separados, se o usuário estiver instalando pela primeira vez, como o app ja esta com o
        * banco em sua versão 2, então as duas modificações ocorrem, a criação da estrutura básica e
        * a atualização */
        if (oldVersion <= 2){
            sql = "ALTER TABLE BEBIDA ADD COLUMN favorita NUMERIC;";
            db.execSQL(sql);
        }

    }
}
