package common.model;

import javax.validation.constraints.Min;

/**<pre>
 * 検索キーのベースクラス。
 * ページ番号やページサイズを持つ。
 * </pre>
 */
public class BaseSearchKeys extends BaseObject {
	@Min(0)
	private int _pagesize = 0;
	@Min(0)
	private int _page = 0;

	public int get_pagesize() {
		return _pagesize;
	}
	public void set_pagesize(int _pagesize) {
		this._pagesize = _pagesize;
	}
	public int get_skiprows() {
		return this._page * this._pagesize;
	}
	/**
	 * ページ番号（0～）
	 * @param _page
	 */
	public void set_page(int _page) {
		this._page = _page;
	}
	public int get_page(){
		return this._page;
	}
}
