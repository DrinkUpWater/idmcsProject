package com.inside.idmcs.api.common.error;

public enum ErrorCode {
	F101("JSON 형식 오류"),
    F102("데이터 오류 (필수 값 누락 [%s])"),
    F103("데이터 오류 (요청 데이터 값 또는 형식 오류 [%s])"),
    F104("허용되지 않은 연계기관"),
    F105("허용되지 않은 앱"),
    F106("허용되지 않은 URL"),
    F107("허용되지 않은 IP"),
    F108("개인키 유효기간 만료"),
    F109("개인키 복호화 오류"),
    F110("개인키 암호화 오류"),
    F111("데이터 오류 (유효하지 않은 신분번호)"),
    F112("데이터 오류 (신분번호 년도, 뒷자리(성별코드) 불일치)"),
    F119("연계 요청 기타 오류"),
    F200("이미 가입한 사용자"),
    F201("미가입 사용자"),
    F202("핸드폰 번호 불일치"),
    F203("단말정보 불일치"),
    F204("appkey 불일치"),
    F205("통신사 불일치"),
    F206("서비스 일시정지 이력 존재(재가입 필요)"),
    F207("서비스 이용기간 만료"),
    F208("연계 토큰 유효기간 만료"),
    F209("연계서비스 가입정보 기타 오류"),
    F301("비정상 직원 상태 (휴직, 퇴직 등)"),
    F302("비정상 신분증 상태(훼손, 회수 등)"),
    F303("신분 정보 오류(성명, 주민번호, 발급일자 등 불일치)"),
    F401("QR코드 생성 오류"),
    F402("QR코드 검증 오류(타 시스템 QR코드 등)"),
    F403("핸드폰 번호 불일치"),
    F404("QR코드 유효기간 만료"),
    F499("QR코드 기타 오류"),
    F501("연계키 발급 오류(기관코드, 토큰 등)"),
    F502("공개키 발급 오류"),
    F801("내부 오류 [%s]"),
    F901("기타 오류");

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String formatMessage(String value) {
        return String.format(this.description, value);
    }
}
