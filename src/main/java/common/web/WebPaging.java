package common.web;

import java.beans.Introspector;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.TypeMismatchException;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;
import common.model.BaseSearchKeys;


/**
 * <pre>
 * WEB画面のページング情報クラス。
 * 検索キークラス（searckKeys）をラップし、WEB画面でページングをできるようにしたクラス。
 * このクラスを使用することで、自動的にページングの情報をクライアントとやりとりできるようになる。
 * このクラスはページ情報を保持すると同時に、クライアント（HTML）とのやりとりを仲立ちする機能である。
 *
 *
 * 【できること】
 * ・次へ・前へもしくは、ページを指定して行うページ制御
 * ・検索画面と無関係のページから検索結果画面に遷移すること。
 *
 *
 * 【使用方法】
 * ・検索キークラスを作成し、このクラスに設定する。
 * 以下の例題を参照。
 *
 * 【例:検索キークラスのサンプル】
 * //検索キークラス
 * class TestSearchKeys extends {@link BaseSearchKeys}{
 *    private int id;
 *    private String name;
 *
 *    //
 *    public int getId(){...}
 *    ...
 * }
 *
 * //検索キーを使用したサンプル（Springでの使用例）
 * class TestActionForm {
 *    private TestSearchKeys searchKeys = new TestSearchKeys();
 *    private WebPaging paging = new WebPaging(searchKeys, 0);
 *    //
 *    public void getSearchKey(){...}
 *    public TestSearchKey getPaging(){...}
 * }
 *
 *
 * 【例：JSPでの使用サンプル（前へ・次へ）】
 * JSTLの場合：
 * &lt;form:form action="list.html" modelAttribute="form" >
 *	&lt;input type="hidden" name="paging.searchKeys" value="${fn:escapeXml(form.paging.prev.searchKeys)}" />
 *	&lt;input type="submit" value="前へ">
 * &lt;/form:form>
 *
 *
 * 【例：ページリンクを複数表示したい場合のサンプル（1・2・3・4のようにページリンクを作る場合）】
 * &lt;c:forEach items="${form.paging.pageList}" var="paging">
 *   &lt;form:form action="list.html" modelAttribute="form" cssStyle="display: inline;">
 *     &lt;input type="hidden" name="paging.searchKeys" value="${fn:escapeXml(paging.searchKeys)}" />
 *     &lt;input type="submit" value="${paging.page}">
 *   &lt;/form:form>
 * &lt;/c:forEach>
 *
 * 【例：ページ番号を指定してジャンプしたい場合の使用サンプル】
 * &lt;form:form action="list.html" method="POST" modelAttribute="form" cssStyle="display: inline;">
 *   &lt;input name="paging.specifiedPageNum" value="${form.paging.page}" style="width:50pt;"/>/${form.paging.maxPage}
 *   &lt;input type="hidden" name="paging.searchKeys" value="${fn:escapeXml(form.paging.pageSpecify.searchKeys)}" />
 *   &lt;input type="submit" value="ジャンプ" />
 * &lt;/form:form>
 *
 * 【仕組み】
 * 内部で保持した検索キークラスの値を全てxstreamでjson文字列に変換してパラメタに保管している。
 * パラメタとして受け取ったときはjsonから元に戻している。
 *
 * </pre>
 *
 */
public final class WebPaging implements Cloneable {
	private static XStream xstream;
	static{
		xstream = new XStream(new JettisonMappedXmlDriver(){
			public HierarchicalStreamWriter createWriter(Writer writer) {
				char[] ret = new char[]{' '};
				return new JsonWriter(writer,
					JsonWriter.STRICT_MODE,
					new JsonWriter.Format(ret, ret, JsonWriter.Format.COMPACT_EMPTY_ELEMENT )
				);
	    }} );
        xstream.setMode(XStream.NO_REFERENCES);
        //xstream.denyTypes(new Class[]{WebPaging.class});
	}

	//
	private String searchKeysClassName;
	@Min(0)
	private int page = 0;
	@Min(1)
	private int maxPage = 1;
	@Min(0)
	private int count = 0;
	@Min(1)
	private int pageListSize = 5;
	@NotNull
	private BaseSearchKeys searchKeys;

	///妥当性チェック用：最大ページ番号
	private int validMaxPage = 100;
	///妥当性チェック用：最大
	private int validMaxPageSize = 100;


	public WebPaging(BaseSearchKeys searchKeys, int count){
		this(searchKeys, count, 10);
	}

	public WebPaging(BaseSearchKeys searchKeys, int count, int pageListMaxSize){
		this(searchKeys, count, searchKeys.get_page() + 1, pageListMaxSize);
	}


	public WebPaging(BaseSearchKeys searchKeys, int count, int page, int pageListMaxSize){
		Assert.notNull(searchKeys, "Constructor argument must not be null.");
		Assert.state(pageListMaxSize > 1, "pageListSize must be pageListSize > 1.");

		//
		this.searchKeys = searchKeys;
		this.page = page;
		int pagesize = searchKeys.get_pagesize();
		putPageCntAndSize(count, pagesize);
		String name = searchKeys.getClass().getSimpleName();
		this.searchKeysClassName = Introspector.decapitalize(name);
		//パッケージ名が分からないようにするため略称を使用する
        xstream.aliasType(name, searchKeys.getClass());
	}

	/**
	 * このクラスとsearchKeysにページ番号とページサイズを設定する。maxSizeも設定される。
	 * @param page　[in]ページ番号（1～）
	 * @param pageSize [in]1ページのサイズ(0のとき無限大。)
	 */
	public void reset(int page, int pagesize){
		setPage(page);
		this.searchKeys.set_page(page - 1);
		putPageCntAndSize(0, pagesize);
	}

	/**
	 * 最大値を設定する。setSearchKeysで妥当性チェックするときに使用する。
	 * @param maxPage
	 * @param maxPageSize
	 */
	public void putValid(int maxPage, int maxPageSize){
		this.validMaxPage = maxPage;
		this.validMaxPageSize = maxPageSize;
	}


	/**
	 * 現在のページ番号(1～)
	 * @return
	 */
	public int getPage() {
		return page;
	}

	/**
	 * このクラスのpageのみに値を設定する
	 * @param page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * 1ページあたりのレコード数。{@link #searchKeys}に設定された値が返ります。
	 * @return
	 */
	public int getPageSize() {
		return this.searchKeys.get_pagesize();
	}
	public void setPageSize(int pagesize){
		//他のフィールドも計算しなおして設定する。
		putPageCntAndSize(this.count, pagesize);
	}

	public int getMaxPage() {
		return maxPage;
	}

	/**
	 * 全検索結果数と１ページのレコード数を設定します。pagesizeは{@link #searchKeys}に設定されます。
	 * @param pageSize
	 */
	public void putPageCntAndSize(int count, int pagesize){
		this.count = count;
		this.searchKeys.set_pagesize(pagesize);
		this.maxPage = (pagesize < 1 ? 1 : ((count-1) / pagesize + 1) );
	}

	/**
	 * this.pageとsearchKeysの両方にページを設定する。
	 * @param page
	 */
	protected void putPage(int page){
		if(page > this.maxPage) page = this.maxPage;
		this.page = page;
		this.searchKeys.set_page(this.page - 1);
	}


	/**
	 * 検索結果すべてのレコード数
	 * @return
	 */
	public int getCount() {
		return count;
	}

	/**
	 * 全ての検索結果レコード数を設定する。
	 * @param count [in]設定する検索結果数
	 */
	public void setCount(int count) {
		//他のフィールでも計算しなおして設定する。
		putPageCntAndSize(count, this.searchKeys.get_pagesize());
	}

	/**
	 * 表示したいページ番号のリストの数（ページ番号 2 3 4  のようなリンクを作りたい場合、3になる）
	 * @return
	 */
	public int getPageListSize() {
		return pageListSize;
	}

	public void setPageListSize(int pageListSize) {
		this.pageListSize = pageListSize;
	}

	/**
	 * プロパティ文字列から値をロードする。
	 * @param keys
	 * @throws TypeMismatchException 書式が違う。もしくはjsonのパースに失敗した場合。
	 */
	public void setSearchKeys(String keys){
		Assert.notNull(this.searchKeys, "searchKeys must not be null.");

		try {
			String[] tmp = keys.split(",", 2);
			String[] pageValues = tmp[0].split(":");

			//ページング情報の設定
			if(pageValues.length != 4) throw new IllegalArgumentException("ページング情報の書式が不正");
			int count = Integer.parseInt(pageValues[0]);
			int page = Integer.parseInt(pageValues[1]);
			this.pageListSize = Integer.parseInt(pageValues[2]);
			int pagesize = Integer.parseInt(pageValues[3]);

			//値のチェック
			if(page < 0 || page > this.validMaxPage){
				throw new IllegalArgumentException("ページ番号の設定が不正: page=" + page);
			}else if(pagesize < 0 || pagesize > this.validMaxPageSize){
				throw new IllegalArgumentException("ページサイズの設定が不正: pageSize=" + pagesize);
			}

			//検索キー情報の設定
			String json = tmp[1];
			xstream.fromXML(json, this.searchKeys);

			//検索キーにpageなどを設定
			putPageCntAndSize(count, pagesize);
			//ページ番号指定をする場合はpageに値を設定しない。
			if(page != 0){
				putPage(page);
			}else{
				putPage(this.page);
			}

		}catch(Exception e){
			throw new TypeMismatchException(keys, this.searchKeys.getClass(), e);
		}
	}



	/**
	 * プロパティ文字列を取得する
	 * @return
	 */
	public String getSearchKeys(){
		try {
			String json = xstream.toXML(this.searchKeys);

			//ページング情報を文字列に変換(検索結果数：ページ番号：ページリスト数：1ページ表示数)
			String pageStr = this.count + ":" + this.page
					+ ":" + this.pageListSize + ":" + getPageSize();

			return pageStr + "," + json;

		} catch (Exception e) {
			throw new RuntimeException("検索キーのパラメタを作成中にエラー発生", e);
		}
	}


	/**
	 * ページ番号を指定して検索したい場合に使用する。
	 * WEBパラメタでページ番号を指定する。
	 * @param page [in]ページ番号（1～）
	 * @note バインダでsetAllowedFieldsで指定すること（例："paging.specifiedPageNum"）
	 */
	public void setSpecifiedPageNum(int page){
		if(page < 0 || page > this.validMaxPage){
			throw new IllegalArgumentException("ページ番号不正：paeg=" + page);
		}
		putPage(page);
	}


	/**
	 * ページ番号を指定して検索したい場合のページングオブジェクト。
	 * @return
	 */
	public WebPaging getPageSpecify(){
		WebPaging p = (WebPaging)clone();
		p.page = 0;
		return p;
	}



	/**
	 * 前のページのページ情報を取得する。
	 * @return
	 */
	public WebPaging getPrev(){
		WebPaging p = (WebPaging)clone();
		--p.page;
		if(p.page < 1) p.page = 1;

		//
		return p;
	}


	/**
	 * 次のページのページ情報を取得する。
	 * @return
	 */
	public WebPaging getNext(){
		WebPaging p = (WebPaging)clone();
		++p.page;
		if(p.page > p.maxPage) p.page = p.maxPage;

		//
		return p;
	}


	/**
	 * 一番最後のページのページ情報を取得する。
	 * @return
	 */
	public WebPaging getLast(){
		WebPaging p = (WebPaging)clone();
		p.page = p.maxPage;
		return p;
	}

	/**
	 * 一番最初のページのページ情報を取得する。
	 * @return
	 */
	public WebPaging getFirst(){
		WebPaging p = (WebPaging)clone();
		p.page = 1;
		return p;
	}


	public boolean isFirst(){
		if(this.page == 1) return true;
		return false;
	}

	public boolean isLast(){
		if(this.page == this.maxPage) return true;
		return false;
	}

	/**
	 * ページ情報のListを取得する。
	 * 例えば、現在のページから前後5ページまでのリンクを表示したい場合に使用する。
	 * @return
	 */
	public List<WebPaging> getPageList(){
		int startPage = this.page - this.pageListSize;
		int endPage = this.page + this.pageListSize;
		if(startPage <= 0) startPage = 1;
		if(endPage > this.maxPage){
			endPage = this.maxPage;
		}
		//開始ページと終了ページからListの数を計算する
		int size = endPage - startPage + 1;

		//開始ページから順番にページをListに追加していく
		List<WebPaging> list = new ArrayList<WebPaging>();
		WebPaging p = this.clone();
		p.page = startPage;
		for(int i=0; i<size; ++i){
			list.add(p);
			p = p.getNext();
		}
		return list;
	}



	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(this.getClass().getSimpleName());
		buf.append("[searchKeysClassName=");
		buf.append(this.searchKeysClassName);
		buf.append(", page=");
		buf.append(this.page);
		buf.append(", pageSize=");
		buf.append(this.getPageSize());
		buf.append(", maxPage=");
		buf.append(this.maxPage);
		buf.append(", count=");
		buf.append(this.count);
		buf.append(", pageListSize=");
		buf.append(this.pageListSize);
		buf.append("]");
		return buf.toString();
	}

	@Override
	protected WebPaging clone() {
		WebPaging p = new WebPaging(this.searchKeys, this.getCount());
		p.maxPage = this.maxPage;
		p.count = this.count;
		p.page = this.page;
		p.pageListSize = this.pageListSize;
		p.validMaxPage = this.validMaxPage;
		p.validMaxPageSize = this.validMaxPageSize;
		return p;
	}
}
