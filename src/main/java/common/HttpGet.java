package common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpGET.java
 * 指定された URL に HTTP 接続し，コンテンツを標準出力に
 * 出力する．コンテンツの取得には GET メソッドを使用する．
 *
 */
public class HttpGet
{
    public static void main(String[] args)
    {
	// 接続先の URL
	String url_str;
	try{
	    url_str = args[0]; // コマンドライン引数を使用
	}
	catch( ArrayIndexOutOfBoundsException e ){
	    // コマンドライン引数が無い場合のデフォルト URL
	    url_str = "http://localhost:28080/test/top.jsp";
	}

	try{
	    URL url = new URL(url_str);

	    // URL 情報を標準エラー出力に出力
	    System.err.println("【プロトコル】 " + url.getProtocol());
	    System.err.println("【 ホ ス ト 】 " + url.getHost());
	    System.err.println("【 ファイル 】 " + url.getFile());
	    System.err.print("【ポート番号】 ");
	    if( url.getPort() == -1 )
		System.err.println( url.getDefaultPort() );
	    else System.err.println( url.getPort() );

	    // HTTP 接続オブジェクトの取得
	    HttpURLConnection http = (HttpURLConnection)url.openConnection();

	    // GET メソッドに設定
	    http.setRequestMethod("GET");
	    http.setRequestProperty("Cookie", "JSESSIONID=86C660AE42D0B9EF6BBD659E685AC88E.tomcat0");
	    //http.setRequestProperty("Cookie", "JSESSIONID=4AA202D7FCB8069B6C0119A9C338D23A.tomcat2");


	    // 接続
	    System.err.print("接続中 ... ");
	    http.connect();
	    System.err.println("成功");

	    // コンテンツの取得と表示
	    System.err.print("コンテンツ取得(出力)中 ... ");
	    BufferedInputStream bis = new BufferedInputStream( http.getInputStream() );
	    byte[] data = new byte[200];
	    while ( bis.read(data) != -1 )
		System.out.write(data);

	    System.err.println("終了");

	    // 応答コード＆メッセージ
	    System.err.println("【応答コード】 " + http.getResponseCode()
			       + " " + http.getResponseMessage() );
	}
	catch( IOException e ){
	    System.err.println("失敗");
	    System.err.println( e );
	}
    }
}

