# Лаборатори №11 — Mutation testing with PIT

**Овог нэр:** Г. Саранчимэг 
**Оюутны код:** B232274001  
**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест  
**Огноо:** 2026

## Орчны мэдээлэл

Энэ ажлыг Windows орчинд суусан JDK 22 болон Maven 3.10.0 ашиглан ажиллуулсан. Төслийн `pom.xml` нь Java 17 release, JUnit 5.10.2, JaCoCo 0.8.12, PIT 1.16.1 ашигладаг.

```text
java version: 22
Apache Maven: 3.10.0
```

## Эхний хэмжилт

`mvn clean verify`-ийн JaCoCo үр дүн:

- Line coverage: 25/25 буюу 100%
- Branch coverage: 38/41 буюу 92.7%

Starter тестийн PIT үр дүн:

- Generated: 63
- Killed: 17
- Mutation coverage: 27%
- Test strength: 27%

Нотолгоо: `results/jacoco-start.csv`, `results/pit-start.txt`.

Line coverage 100% байгаа нь бүх мөрөөр дор хаяж нэг удаа явсныг хэлдэг боловч тухайн мөрийн буцаасан утгыг зөв шалгасан гэсэн үг биш. PIT-ийн 27% score нь сул assertion-ууд олон буруу зан төлөвийг илрүүлж чадахгүй байсныг харуулсан.

## A. Амьд мутантуудын ангилал

Starter-ийн амьд мутантуудын гол ангилал:

| Мөр/хэсэг | PIT мутаци | Ангилал | Арга хэмжээ, үндэслэл |
|---|---|---|---|
| `letterGrade`-ийн заагууд | `CONDITIONALS_BOUNDARY` (`>=`/`>` төрлийн өөрчлөлтүүд) | Тест дутуу | Заагийн яг утгууд (`60`, `70`, `80`, `90`, мөн `59.99`, `69.99`, `79.99`, `89.99`) эхэнд байгаагүй тул бодит зан төлөвийн өөрчлөлтийг тест бариагүй. |
| `scholarship`-ийн GPA/credit нөхцөл | `CONDITIONALS_BOUNDARY` | Тест дутуу | `3.8/15`, `3.4/12`, `3.0` болон өмнөх утгуудын тест байгаагүй тул нөхцөлийн заагийн мутациуд амьд үлдсэн. |
| `gpa` switch | `EXPERIMENTAL_SWITCH` болон conditional equality мутациуд | Тест дутуу / equivalent | Бүх үсгэн дүнг нэг бүрчлэн шалгах тест дутуу байсан; B2-д бүх A/B/C/D/F утгыг шалгасан. Үлдсэн дөрвөн `REMOVE_CONDITIONALS_EQUAL_ELSE` нь switch-ийн bytecode dispatch дээрх зан төлөвийг өөрчлөөгүй тул equivalent гэж ангилсан. |
| `letterGrade`, `gpa`, `scholarship` return утгууд | `PRIMITIVE_RETURNS`, `EMPTY_RETURNS`, `TRUE/FALSE_RETURNS` | Тест дутуу | `assertNotNull` болон зөвхөн муж шалгасан assertion-ууд буруу return утгыг зөвшөөрч байсан; exact `assertEquals` болгосноор устгасан. |
| `scholarship` нөхцлийн дараалал | `REMOVE_CONDITIONALS_ORDER_IF/ELSE` | Тест дутуу | Олон нөхцөл зэрэг үнэн болох жишээ болон яг онооны үр дүнг эхний тестүүд шалгаагүй; exact assertion ба boundary тестээр устгасан. |

Equivalent гэж ангилахдаа зөвхөн тухайн өөрчлөлт нь observable output-ийг өөрчлөхгүй, ямар ч оролт дээр ялгаатай үр дүн үүсгэхгүй гэж үзсэн мутантуудыг хамруулсан. Бусад амьд мутантуудыг тест дутуу гэж үзэж, B1/B2-д тест нэмсэн.

## B. Тестийг хүчжүүлсэн явц

### B1 — Exact assertion

Сул `assertNotNull` болон `assertTrue(value >= 0 && value <= 100)` assertion-уудыг бодит oracle утгатай `assertEquals` болгосон. Үүний дараах PIT хэмжилт:

| Үе шат | Generated | Killed | Score |
|---|---:|---:|---:|
| Starter | 63 | 17 | 27% |
| B1 | 71 | 49 | 69% |
| B2 | 71 | 67 | 94% |

Нотолгоо: `results/pit-start.txt`, `results/pit-B1.txt`, `results/pit-B2.txt`. B1-ийн дараа return value, switch, math болон conditional order төрлийн олон мутант устсан.

### B2 — Boundary болон бүх салааны тест

Дараах тестүүдийг нэмсэн:

- Letter grade-ийн `0`, `100`, `60`, `70`, `80`, `90` болон бүх заагийн өмнөх `59.99`, `69.99`, `79.99`, `89.99` утгууд;
- GPA-ийн A, B, C, D, F бүх switch case;
- Scholarship-ийн `3.8/15`, `3.4/12`, `3.0`, өмнөх утгууд, `gpa=0`, `credits=0`, violation болон оролтын хязгаарууд.

Эцсийн PIT score 94% болсон бөгөөд даалгаврын 85%-ийн зорилтыг хангасан. Үлдсэн 4 мутант нь switch-ийн equivalent `REMOVE_CONDITIONALS_EQUAL_ELSE` мутантууд гэж ангилагдсан.

## C. PIT-гүй гараар хийсэн мутант

`scholarship()` доторх:

```java
if (gpa >= 3.8 && credits >= 15) return 100;
```

гэсэн нөхцөлийг түр хугацаанд `gpa > 3.8` болгож ажиллуулсан. `mvn test` үед `scholarship_checksGpaAndCreditBoundaries()` тест унаж, `3.8` GPA ба `15` credit дээр `expected: <100> but was: <70>` гэсэн алдаа гарсан. Дараа нь `src/main`-ийг буцааж сэргээсэн. Бүрэн output нь `results/mvn-test-manual-mutant.txt` файлд байна.

Өөрийн equivalent мутантын жишээ болгон switch-ийн equality dispatch дээрх `REMOVE_CONDITIONALS_EQUAL_ELSE` өөрчлөлтийг авч үзсэн. Энэ өөрчлөлт нь PIT-ийн тайланд үлдсэн боловч method-ийн observable GPA output-ийг ямар ч шалгагдсан оролт дээр өөрчлөөгүй тул equivalent гэж үзсэн.

## Дүгнэлт

Line coverage нь кодын мөрүүдээр явсан эсэхийг хэмждэг, харин mutation score нь тестүүд буруу кодын өөрчлөлтийг илрүүлж чадсан эсэхийг хэмждэг. Тиймээс line coverage 100% байсан ч starter тестийн mutation score ердөө 27% байв. Exact assertion нэмсний дараа score 69% болж, зааг болон бүх салааны тест нэмсний дараа 94% болсон. Boundary тестүүд нь `>=`-г `>` болгосон зэрэг бодит алдааг илрүүлэхэд онцгой чухал байлаа. Equivalent мутант нь зан төлөвийг өөрчилдөггүй тул тестээр устгах боломжгүй, гэхдээ ийм ангиллыг болгоомжтой хийх хэрэгтэй. AI-аар тест үүсгүүлсэн үед кодын бүх мөрийг хамарсан эсэхээс гадна PIT score-оор тестийн чанарыг шалгах шаардлагатай. AI ихэвчлэн ерөнхий assertion үүсгэж болох тул exact output, boundary, exception, бүх branch-ийг тусад нь баталгаажуулах хэрэгтэй.


`target/` хавтас `.gitignore`-д орсон бөгөөд repository-д results-ийн текст нотолгоонуудыг хадгалсан.
