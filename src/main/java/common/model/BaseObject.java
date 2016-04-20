package common.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;



/**
 * ドメインオブジェクトのベースクラスです<br>
 * <br>
 */
public abstract class BaseObject {

	/** 数値の変数に値が設定されていないこと示す値。 初期値でこの値を設定しておく。*/
	public static final int NOT_SETTED_VALUE = -1;

	protected BaseObject() {}

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}