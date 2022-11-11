package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),

    JOURNEY_JWT_CHECK_ERROR(false,2018,"jwt에서 추출한 유저아이디와 여정아이디에서 추출한 유저아이디가 다릅니다."),
    PLANET_JWT_CHECK_ERROR(false,2019,"jwt에서 추출한 유저아이디와 행성아이디에서 추출한 유저아이디가 다릅니다."),
    WITHDRAW_USER(false,2020,"탈퇴한 유저아이디가 입력되었습니다."),
    EMPTY_PLANET_LIST(false,2021,"행성목록이 비어있습니다.."),
    EMPTY_PERIOD(false,2022,"기간이 비어있습니다.."),
    DELETED_PLANET(false,2023,"삭제된 행성입니다."),
    END_JOURNEY(false,2024,"끝난 여정아이디 입니다."),
    EMPTY_PLANET_NAME(false,2025,"행성이름이 비어있습니다.."),
    DUPLICATE_PLANET_NAME(false,2026,"중복되는 행성이름입니다..."),
    EMPTY_DETAILED_PLANS(false,2027,"세부계획목록이 비어있습니다..."),
    DUPLICATE_PLAN(false,2028,"중복된 계획이 존재합니다."),
    EMPTY_PLAN_CONTENT(false,2029,"계획내용이 비어있습니다..."),
    EMPTY_TYPE(false,2030,"타입이 비어있습니다..."),

    FAIL_FILE_UPLOAD(false,2031,"파일업로드 실패"),
    EMPTY_CODE(false,2032,"인증번호가 비어있습니다."),

    WRONG_TYPE_DAY(false,2033,"요일입력값의 타입이 틀렸습니다."),
    WRONG_JWT(false,2034,"입력된jwt의 유저가 해당 계획의 주인이 아닙니다."),

    ALREADY_DELETED(false,2035,"이미 삭제된 세부계획입니다."),
    DUPLICATED_PLANET_NAME(false,2036,"중복된 행성이름이 존재합니다."),
    EMPTY_DETAILED_PLAN(false,2037,"세부계획이 빈값입니다."),
    EMPTY_KEYWORDS(false,2038,"키워드가 비어있습니다."),
    WRONG_TYPE(false,2039,"잘못된 타입값입니다."),
    UPDATE_FAILED(false,2040,"값 업데이트가 실패하였습니다."),
    PLANET_ERROR(false,2041,"해당없음 행성은 1회성 타입만 선택가능합니다."),
    CANNOT_COMPLETED(false,2042,"마음가짐타입은 완료처리할 수 없습니다."),
    CANNOT_ACCESS(false,2043,"해당 행성(해당없음 행성)은 접근할 수 없습니다."),
    SHEDULE_ERROR(false,2044,"매일 자동생성되어야하는 데이터가 생성되지않았거나 , 문제발생"),
    NOT_EXISTS_COMPLETED_PLAN(false,2045,"today_complted_plans에 데이터가 없습니다. "),
    EMPTY_NICKNAME(false,2046,"닉네임값이 비어있습니다."),
    DUPLICATE_NICKNAME(false,2047,"중복된 닉네임입니다."),
    EMPTY_CONTENT_AND_TYPE(false,2048,"세부계획의 명과 타입 둘다 빈값입니다."),

    CANNOT_CHANGE_TYPE(false,2049,"오늘완료한 세부계획의 타입은 수정할수 없습니다.(데이터가 꼬임)"),
    WRONG_COLOR_TYPE(false,2050,"입력된 컬러값이 올바르지않습니다."),
    NOT_EXISTS_DATE_DATA(false,2051,"해당 유저의 데이터가없습니다 서버에 문의하세요!"),
    DELETED_PLAN(false,2052,"삭제된 세부계획입니다."),






    START_DATE_ERROR(false, 2501,"시작날짜를 입력해주세요"),
    END_DATE_ERROR(false, 2502,"마지막날짜를 입력해주세요"),
    DATE_NUM_ERROR(false,2503,"날짜에 숫자를 입력해주세요"),
    ACCESSTOKEN_ERROR(false,2504,"액세스토큰을 입력해주세요"),
    INVALID_JOURNEY_USER(false,2505,"입력한 여정에 대한 유저가 없습니다."),
    POST_PHONE_NUM_TYPE(false,2506,"휴대폰 번호를 확인해주세요"),
    EMPTY_PLANET_COLOR(false,2507,"행성의 색깔을 입력 해 주세요"),
    POST_USERS_EMPTY_PASSWORD(false,2508 , "패스워드를 입력 해 주세요"),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),
    DELETE_DIARY_ERROR(false, 4501, "다이어리 삭제에 실패하였습니다.");





    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
