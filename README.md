# 옵시디언, Obsidian Clone Project


## 👨‍💻 팀원 소개
| 이승준 | 노영재 | 이서연 | 마서영 | 이정민 |
| --- | --- | --- | --- | --- |
| <img src="https://avatars.githubusercontent.com/sengjun0624" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/YoungjaeRo" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/u/90055686?v=4" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/luxihua" width="150" height="150"> | <img src="https://avatars.githubusercontent.com/jeongmin07262" width="150" height="150"> |
| BE | BE | BE | FE | FE |


<br><br>

## 💡 프로젝트 소개
- 우리 FISA 4기 클라우드 서비스반을 위한 Private Obsidian
<br>
- 더욱 저렴하게 옵시디언을 이용해보세요!

<br><br>

<!--## 📌 상세 페이지
### 공통,메인 페이지
<div><img src="https://private-user-images.githubusercontent.com/76603301/410735306-ed479451-7512-4611-9953-22f093cba2d9.gif?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Mzg4OTY1MTUsIm5iZiI6MTczODg5NjIxNSwicGF0aCI6Ii83NjYwMzMwMS80MTA3MzUzMDYtZWQ0Nzk0NTEtNzUxMi00NjExLTk5NTMtMjJmMDkzY2JhMmQ5LmdpZj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTAyMDclMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMjA3VDAyNDMzNVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTcwNzY2MjNmMWExYzMxMzM3Y2MxNjk5OTI5ZGM3NjRjOTJmOGY0M2ExYmE2MjAwNmU2MjAwZDg1MTNiZjIxNjUmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.hoU7XI54sG1v-feL-9xCt88RrLfPuzHIlgMWf9vJ5zY" width="350" height="600">
</div>

- 로고(홈 페이지로 이동), 채팅으로 이동 할 수 있는 공통 헤더
- 홈, 랭킹, 타임라인, 프로필로 이동 할 수 있는 공통 네브바
- 시작 화면인 메인 페이지
- Tailwind CSS와 함께 활용 가능한 **`Heroicons`** 라이브러리 사용
- **[추가 구현 예정]** 메인페이지에서 시작하기 버튼을 누르면 로그인 페이지로 이동, 저속노화 알아보기 버튼을 누르면 관련된 뉴스 등을 보여주는 페이지로 이동
<br>

### 캘린더
<div>
    <img src="https://private-user-images.githubusercontent.com/76603301/410734923-24ba1ffe-5e24-43fb-b54d-cb8c8468be61.gif?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Mzg4OTY2MjMsIm5iZiI6MTczODg5NjMyMywicGF0aCI6Ii83NjYwMzMwMS80MTA3MzQ5MjMtMjRiYTFmZmUtNWUyNC00M2ZiLWI1NGQtY2I4Yzg0NjhiZTYxLmdpZj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTAyMDclMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMjA3VDAyNDUyM1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTc2ODk1YzVkZmI4ZWM5Y2I4Y2UxMWQ5ZjUwZDkzMjA5M2NiY2U3MTVjNjlhOWNjMTMxMmNhZWQ4OWU3ZDkyZmImWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.prEwUZfXl8JFkDAACOapQrJMHMc2riOkADBFBnhqPt0" width="350" height="600">
</div>


- 달력을 통해 날짜별 타임라인 요약을 확인할 수 있는 기능
- **`react-calendar`** 라이브러리를 사용하여 커스텀하여 적용
- 채팅이 있는 날 달력의 마킹으로 채팅 유무를 시각적으로 확인 가능
- **[추가 구현 예정]** 날짜 선택시 타임라인이 있는날은 타임라인이 나오게, 없는 날은 채팅 시작 창으로 이동하게 구현
<br>

### 타임라인 
<div>
    <img src="https://private-user-images.githubusercontent.com/76603301/410735182-1c61d9a4-8124-4168-b404-591346795cd7.gif?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Mzg4OTY1NDYsIm5iZiI6MTczODg5NjI0NiwicGF0aCI6Ii83NjYwMzMwMS80MTA3MzUxODItMWM2MWQ5YTQtODEyNC00MTY4LWI0MDQtNTkxMzQ2Nzk1Y2Q3LmdpZj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTAyMDclMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMjA3VDAyNDQwNlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWUwMTRlYWZiMTNiY2JmMWE1OTZhOTNhY2U0ODlhNWJkNThlNDkzMjVkZWNkNGZiMDA1MDNmNTRjNTYwZWM0ZTAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.zEKh0vU8xuUjfn5dyPBD6BWmucZ9Co6Y9civA6IIjgI" width="350" height="600">
</div>

- 날짜를 클릭하면 해당 날짜의 타임라인 컴포넌트 보여주는 기능
- 타임라인 컴포넌트를 클릭하면 해당 타임라인의 요약내용을 모달창으로 통해 보여주는 기능
- **[추가 구현 예정]** 타임라인의 요약 내용 + 채팅 내용까지 보여주게 구현
<br>

### 채팅
<div>
    <img src="https://private-user-images.githubusercontent.com/76603301/410735501-f51a5c02-8952-43b4-955b-717d116a7682.gif?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3Mzg4OTY0ODYsIm5iZiI6MTczODg5NjE4NiwicGF0aCI6Ii83NjYwMzMwMS80MTA3MzU1MDEtZjUxYTVjMDItODk1Mi00M2I0LTk1NWItNzE3ZDExNmE3NjgyLmdpZj9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTAyMDclMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwMjA3VDAyNDMwNlomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWIzMTlkNGQ1YjFkYjAwOWZiZWJhYWU5NTc5YTQ3Zjk1ZGQ2NjkyZTcyNzBjOWY1Y2RhZjJkOTZiNGM1NjYzYmUmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.eJ4KRz26i9VxwibkHepyy3uugEpf_k24tTf2ryK6Y-Y" width="350" height="600">
</div>

- ChatGPT API를 활용한 실시간 대화
    - ChatGPT 응답을 청크 단위로 바로 렌더링
    - **`"react-markdown"`** 라이브러리를 활용하여 ChatGPT 응답을 마크다운 형식으로 렌더링하게 하여 가독성 향상
    - ChatGPT API를 활용한 실시간 대화 API 및 프롬프트 작성
- 대화 내역 요약 기능
    - 특정 대화량 이상이면 요약 버튼 활성화
    - **`supabase.realtime.on()`을 결합**하여 **프론트엔드에서 실시간 UI 업데이트**
- **[추가 구현 예정]** ChatGPT 대화 내역 history를 적절하게 잘라서 보내기
-->
<br><br>

## 📌 기능 요구사항
### 1. 관리자 기능
1. 관리자는 웹에서 마크다운 에디터를 통해 .md 파일을 작성, 생성할 수 있음,웹 에디터를 적용할 수 있는 라이브러리 활용 필요
2. 관리자는 정적 리소스를 호스팅할 수 있음 ex. .md, .jpg, .png, .webp 등
3. jpg, png와 같은 정적 리소스는 .md 파일 내에 포함되어 퍼블리싱될 수 있음
   ex. 텍스트가 작성된 .md 파일 내에 이미지도 포함되어 화면에 출력됨
4. 관리자는 작성한 .md 파일을 배포(Publish)/회수(Unpublish) 할 수 있음
   ex. 현재 배포된 페이지 목록 조회 후 간단한 체크 버튼(ex. 배포/회수) 등을 통해 배포/회수할 파일 선택
5. 배포할 파일들은 폴더를 구분하여 배포할 수 있음
    ex. JavaScript는 1. Client Side 폴더 내 3.JavaScript 폴더 내에 구성할 수 있음
              1. Client Side/
                    3.JavaScript
                        1.JavaScript Overview.md
### 2. 사용자 기능
1. 사용자는 처음 사용자의 경우 비밀번호를 알아야 페이지에 접근할 수 있음
   ex. 해당 페이지에 대한 초기 사용자 경우 루트 페이지 접속 시 먼저 비밀번호를 물어보는 페이지 출력
2. 사용자는 페이지 내에서 검색 기능을 통해 원하는 내용이 포함된 컨텐츠를 검색할 수 있음
   ex. .md 파일 제목이나 각 파일 내에 작성된 특정 키워드나 텍스트
     1. 검색 기능은 Ctrl + F와 같은 단축키로도 이용할 수 있음
3. 다크모드
4. (1. 관리자 기능의 5.과 연계되는 기능) 사용자는 각 컨텐츠를 관리자가 구분한 폴더별로 구분하여 접근할 수 있음


<br><br>

## 🚧 협업 규칙
### 1. 협업 전략
- `dev`브랜치에서 feat/{작업내용}으로 분기하여 모든 코드는 `dev` 브랜치에 통합하고, 최종 배포 시 `main` 브랜치에 반영합니다.
- **작업 프로세스**:
    - (1) **이슈 발생**: 작업내용(Todo)을 기재합니다.
    - (2) **브랜치 생성**: 이슈 번호를 기반으로 작업 브랜치를 생성합니다.
    - (3) **코드 작성**: 브랜치에서 작업 후 변경사항을 커밋합니다.
    - (4) **Pull Request**: `dev` 브랜치로 병합을 요청합니다.
<br>
 
### 2. Branch 이름
{feat/fix}-{개발 기능}

- ex) `feat/markdown`
