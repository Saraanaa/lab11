package mn.edu.must.sqat;

// ЛАБ 11-ийн ЭХЛЭЛИЙН тестийн багц.
// АНХААР: Энэ багц JaCoCo-гоор Line 100% (Branch 38/41) өгдөг —
// гэхдээ assertion-ууд нь ЗОРИУДААР СУЛ бичигдсэн.
// Таны даалгавар: PIT-ээр амьд үлдсэн мутантуудыг олж,
// тестүүдийг ХҮЧТЭЙ болгож mutation score-ийг өсгөх.

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScholarshipCalculatorTest {

    ScholarshipCalculator calc;

    @BeforeEach
    void setUp() { calc = new ScholarshipCalculator(); }

    // ---- letterGrade: бүх салааг дайрдаг, гэхдээ юу ч нарийн шалгадаггүй ----

    @Test
    void letterGrade_returnsExpectedGradeForEachRange() {
        assertEquals("A", calc.letterGrade(95));
        assertEquals("B", calc.letterGrade(85));
        assertEquals("C", calc.letterGrade(75));
        assertEquals("D", calc.letterGrade(65));
        assertEquals("F", calc.letterGrade(30));
    }

    @Test
    void letterGrade_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-1));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(101));
    }

    @Test
    void letterGrade_checksEveryBoundary() {
        assertEquals("F", calc.letterGrade(0));
        assertEquals("F", calc.letterGrade(59.99));
        assertEquals("D", calc.letterGrade(60));
        assertEquals("D", calc.letterGrade(69.99));
        assertEquals("C", calc.letterGrade(70));
        assertEquals("C", calc.letterGrade(79.99));
        assertEquals("B", calc.letterGrade(80));
        assertEquals("B", calc.letterGrade(89.99));
        assertEquals("A", calc.letterGrade(90));
        assertEquals("A", calc.letterGrade(100));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(-0.01));
        assertThrows(IllegalArgumentException.class, () -> calc.letterGrade(100.01));
    }

    // ---- gpa: давталт, switch-ийн бүх салаа — гэхдээ зөвхөн муж шалгана ----

    @Test
    void gpa_returnsExpectedAverageForRepresentativeGrades() {
        double g1 = calc.gpa(new double[]{95, 85});
        double g2 = calc.gpa(new double[]{75, 65});
        double g3 = calc.gpa(new double[]{40});
        assertEquals(3.5, g1, 0.0001);
        assertEquals(1.5, g2, 0.0001);
        assertEquals(0.0, g3, 0.0001);
    }

    @Test
    void gpa_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(null));
        assertThrows(IllegalArgumentException.class, () -> calc.gpa(new double[]{}));
    }

    @Test
    void gpa_usesEveryLetterGradeValue() {
        assertEquals(2.0, calc.gpa(new double[]{95, 85, 75, 65, 30}), 0.0001);
    }

    // ---- scholarship: бүх дүрмийн салаа — гэхдээ зөвхөн муж шалгана ----

    @Test
    void scholarship_returnsExpectedPercentForRepresentativeCases() {
        int s1 = calc.scholarship(3.9, 16, false);
        int s2 = calc.scholarship(3.5, 13, false);
        int s3 = calc.scholarship(3.1, 5, false);
        int s4 = calc.scholarship(2.0, 1, false);
        int s5 = calc.scholarship(4.0, 20, true);
        assertEquals(100, s1);
        assertEquals(70, s2);
        assertEquals(50, s3);
        assertEquals(0, s4);
        assertEquals(0, s5);
    }

    @Test
    void scholarship_invalidInputs_throw() {
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.1, 10, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(3.0, -1, false));
    }

    @Test
    void scholarship_checksGpaAndCreditBoundaries() {
        assertEquals(100, calc.scholarship(3.8, 15, false));
        assertEquals(70, calc.scholarship(3.8, 14, false));
        assertEquals(70, calc.scholarship(3.4, 12, false));
        assertEquals(50, calc.scholarship(3.4, 11, false));
        assertEquals(50, calc.scholarship(3.0, 0, false));
        assertEquals(0, calc.scholarship(2.99, 20, false));
        assertEquals(0, calc.scholarship(0, 0, false));
        assertEquals(0, calc.scholarship(4, 0, true));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(-0.01, 0, false));
        assertThrows(IllegalArgumentException.class, () -> calc.scholarship(4.01, 0, false));
    }
}
