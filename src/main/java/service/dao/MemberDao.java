package service.dao;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import service.business.model.Member;
import service.business.model.MemberSearchKeys;


/*
会員情報のDAO

ログインユーザのテーブル。本当は管理者・オペレータと会員のテーブルは分けるべきだが、簡易にするため一緒にしています。
【定義】
CREATE DATABASE test
  WITH OWNER = postgres
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'Japanese, Japan'
       LC_CTYPE = 'Japanese, Japan'
       CONNECTION LIMIT = -1;

CREATE TABLE t_member
(
  id integer NOT NULL,
  name character varying(20) NOT NULL,
  age integer NOT NULL,
  up_date timestamp with time zone NOT NULL,
  role character varying(20) NOT NULL,
  login_id character varying(20) NOT NULL,
  login_pw character varying(20) NOT NULL,
  version integer NOT NULL DEFAULT 0,
  CONSTRAINT t_member_pkey PRIMARY KEY (id ),
  CONSTRAINT t_member_login_id_key UNIQUE (login_id )
)
WITH (
  OIDS=FALSE
);

CREATE SEQUENCE seq_member_id
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;


【サンプルデータ登録】
insert into t_member(id, name, age, login_id, login_pw, role, up_date)
values(1, '太郎', 21, 'taro', 'taro', 'ROLE_ADMIN', now());
insert into t_member(id, name, age, login_id, login_pw, role, up_date)
values(2, '華', 20, 'hana',  'hana', 'ROLE_UPDATE', now());
*/
@Repository
public interface MemberDao {
	/**
	 * IDからメンバーオブジェクトを取得する。
	 * @param id
	 * @return 取得したメンバー。見つからない場合はNULL。
	 */
	public Member obtainMember(int id);

	/**
	 * 検索キーにマッチしたメンバーを取得する
	 * @param searchKeys
	 * @return マッチしたメンバー。1件もない場合は0件のListを返す。
	 */
	public List<Member> findMembers(MemberSearchKeys searchKeys);

	/**
	 * 検索キーにマッチしたメンバーの数を取得する。
	 * @param searchKeys
	 * @return
	 */
	public int countMembers(MemberSearchKeys searchKeys);

	/**<pre>
	 * メンバーを更新する。バージョンが楽観的ロックのキーになっており、IDとバージョンが一致するレコードが見つからない場合は
	 * 楽観的ロック例外を投げる。
	 * </pre>
	 * @param user [in]更新するメンバー
	 * <br>　　　[out]更新日に現在日時、インクリメントされたバージョンが設定されたオブジェクト
	 * @throws OptimisticLockingFailureException 楽観的ロックエラー
	 */
	public void updateMember(Member user) throws OptimisticLockingFailureException;

	/**新規メンバーの登録をする。
	 * 失敗した場合は例外を投げる。このとき、ID、更新日は不正になっている可能性がある。
	 * @param user [in]挿入するメンバー
	 * <br>　　　[out]ID,更新日が設定されたオブジェクト。
	 */
	public void insertMember(Member user);

}
