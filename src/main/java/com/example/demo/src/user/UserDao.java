package com.example.demo.src.user;


import com.example.demo.src.kakao.model.PostOAuthReq;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /*
    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select * from UserInfo";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password"))
                );
    }

    public List<GetUserRes> getUsersByEmail(String email){
        String getUsersByEmailQuery = "select * from UserInfo where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                getUsersByEmailParams);
    }

     */

    public GetUserRes getUser(int user_id){
        String getUserQuery = "select email,user_name,profile_url from user where user_id = ?";
        int getUserParams = user_id;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("email"),
                        rs.getString("user_name"),
                        rs.getString("profile_url")),
                getUserParams);
    }
    

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into user (email, password, user_name,phone_num) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getEmail(),postUserReq.getPassword(),postUserReq.getUser_name(),postUserReq.getPhone_num()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update UserInfo set userName = ? where userIdx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select user_id,password,user_name,phone_num from user where email = ?";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("user_id"),
                        rs.getString("password"),
                        rs.getString("user_name"),
                        rs.getString("phone_num")
                ),
                getPwdParams
                );

    }


    public int deleteUser(int user_id) {
        String deleteUserQuery = "update user set status = 0 where user_id =? and status = 1";
        int deleteUserParams = user_id;
        return this.jdbcTemplate.update(deleteUserQuery,deleteUserParams);
    }

    public int isKakaoUser(String kakao_id) {
        String Sql = "select count(kakao_id) from user where kakao_id= ?";
        String Param = kakao_id;
        return this.jdbcTemplate.queryForObject(Sql,int.class,Param);
    }

    public int createKakao(PostOAuthReq postOAuthReq) {
        String Sql = "insert into user (kakao_id,email, password, user_name,phone_num) VALUES (?,?,'akjkz15434ajk',?,01012345678)";
        Object[] Params = new Object[]{postOAuthReq.getKakao_id(),postOAuthReq.getEmail(),postOAuthReq.getName()};
        this.jdbcTemplate.update(Sql,Params);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int KakaoUserInfo(String id) {
        String Sql = "select user_id from user where kakao_id = ? and status = 1";
        String Param = id;
        return this.jdbcTemplate.queryForObject(Sql,int.class,Param);
    }
}
