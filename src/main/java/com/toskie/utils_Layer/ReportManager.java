package com.toskie.utils_Layer;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.jfree.chart.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportManager {

    private static final String BASE = System.getProperty("user.dir");

    // ── Color palette ─────────────────────────────────────────────────────────
    private static final DeviceRgb C_NAVY   = new DeviceRgb(25,  55,  110);
    private static final DeviceRgb C_BLUE   = new DeviceRgb(41,  128, 185);
    private static final DeviceRgb C_GREEN  = new DeviceRgb(39,  174, 96);
    private static final DeviceRgb C_RED    = new DeviceRgb(192, 57,  43);
    private static final DeviceRgb C_AMBER  = new DeviceRgb(211, 84,  0);
    private static final DeviceRgb C_GRAY   = new DeviceRgb(52,  73,  94);
    private static final DeviceRgb C_LGRAY  = new DeviceRgb(236, 240, 241);
    private static final DeviceRgb C_WHITE  = new DeviceRgb(255, 255, 255);
    private static final DeviceRgb C_DKGRAY = new DeviceRgb(60,  60,  60);
    private static final DeviceRgb C_BORDER = new DeviceRgb(189, 195, 199);

    private static ExtentReports extentReports;
    private static String        reportPath;

    // Per-thread test node -- thread-safe for parallel TestNG runs
    private static final ThreadLocal<ExtentTest> extentTestHolder = new ThreadLocal<>();

    // ── Extent test node access ───────────────────────────────────────────────
    public static ExtentTest getTest() { return extentTestHolder.get(); }

    public static synchronized ExtentTest createTestEntry(String testName) {
        if (extentReports == null) createExtentReport();
        ExtentTest test = extentReports.createTest(testName);
        extentTestHolder.set(test);
        return test;
    }

    // ── Report initialisation ─────────────────────────────────────────────────
    public static synchronized void createExtentReport() {
        String dt = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
        new File(BASE + "/Reports/HTML").mkdirs();
        reportPath = BASE + "/Reports/HTML/Toskie_Web_Playwright_Report_" + dt + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Toskie Web Automation Report");
        spark.config().setReportName("Toskie Web Automation");
        spark.config().setJs(
                "document.querySelector('.navbar-brand').innerHTML = " +
                "'<img src=\"https://dev.app.toskie.com/auth/image.svg\" style=\"height:40px;\">';");
        spark.config().setCss(".navbar-brand { display:flex; align-items:center; gap:10px; }");

        extentReports = new ExtentReports();
        extentReports.attachReporter(spark);
        extentReports.setSystemInfo("Project Name",          "Toskie Web Automation");
        extentReports.setSystemInfo("Framework",             "Playwright + Java");
        extentReports.setSystemInfo("Playwright Version",    "1.53.0");
        extentReports.setSystemInfo("Java Version",          System.getProperty("java.version"));
        extentReports.setSystemInfo("OS",                    System.getProperty("os.name"));
        extentReports.setSystemInfo("Execution Date",        new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));
        extentReports.setSystemInfo("Execution Time",        new SimpleDateFormat("HH:mm:ss z").format(new Date()));
        extentReports.setSystemInfo("Report To",             "Abhishek Satyam");
        extentReports.setSystemInfo("QA Automation Tester",  "Santosh Kumar Gupta");
    }

    public static String getReportPath() { return reportPath; }

    public static void flush() {
        if (extentReports != null) extentReports.flush();
    }

    // ── Charts ────────────────────────────────────────────────────────────────
    public static void generatePieChart(int passed, int failed, int skipped) {
        try {
            System.setProperty("java.awt.headless", "true");
            new File(BASE + "/Reports/Charts").mkdirs();
            DefaultPieDataset<String> ds = new DefaultPieDataset<>();
            ds.setValue("Passed",  passed);
            ds.setValue("Failed",  failed);
            ds.setValue("Skipped", skipped);
            JFreeChart chart = ChartFactory.createPieChart("Test Execution Summary", ds, true, true, false);
            PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
            plot.setSectionPaint("Passed",  new Color(39,  174, 96));
            plot.setSectionPaint("Failed",  new Color(192, 57,  43));
            plot.setSectionPaint("Skipped", new Color(230, 126, 34));
            plot.setShadowPaint(null);
            plot.setBackgroundPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.WHITE);
            ChartUtils.saveChartAsPNG(new File(BASE + "/Reports/Charts/piechart.png"), chart, 700, 380);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void generateBarChart(int passed, int failed, int skipped) {
        try {
            System.setProperty("java.awt.headless", "true");
            new File(BASE + "/Reports/Charts").mkdirs();
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            ds.addValue(passed,  "Passed",  "Passed");
            ds.addValue(failed,  "Failed",  "Failed");
            ds.addValue(skipped, "Skipped", "Skipped");
            JFreeChart chart = ChartFactory.createBarChart("Execution Status", "Status", "Count", ds);
            CategoryPlot p = chart.getCategoryPlot();
            BarRenderer r = new BarRenderer();
            r.setSeriesPaint(0, new Color(39,  174, 96));
            r.setSeriesPaint(1, new Color(192, 57,  43));
            r.setSeriesPaint(2, new Color(230, 126, 34));
            r.setShadowVisible(false);
            p.setRenderer(r);
            p.setBackgroundPaint(Color.WHITE);
            chart.setBackgroundPaint(Color.WHITE);
            ChartUtils.saveChartAsPNG(new File(BASE + "/Reports/Charts/barchart.png"), chart, 700, 380);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Main PDF generator ────────────────────────────────────────────────────
    public static void generateFinalReports(int passed, int failed, int skipped,
                                             List<String[]> testResults,
                                             long suiteStartMs, long suiteElapsedMs) {
        System.out.println("[ReportManager] Generating final reports...");
        Document document = null;
        String   pdfPath  = null;
        try {
            flush();
            new File(BASE + "/Reports/PDF").mkdirs();
            generatePieChart(passed, failed, skipped);
            generateBarChart(passed, failed, skipped);

            String ts = new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(new Date());
            pdfPath   = BASE + "/Reports/PDF/Playwright_Report_" + ts + ".pdf";

            PdfWriter   writer = new PdfWriter(pdfPath);
            PdfDocument pdf    = new PdfDocument(writer);
            document           = new Document(pdf);
            document.setMargins(36, 36, 36, 36);

            int    total    = passed + failed + skipped;
            double passRate = total > 0 ? (passed * 100.0 / total) : 0.0;
            int    health   = computeHealthScore(passed, failed, total);

            addCoverHeader(document);
            addExecutiveSummary(document, suiteStartMs, suiteElapsedMs, passRate);
            addDashboard(document, total, passed, failed, skipped, passRate, suiteElapsedMs);
            addClassSummary(document, testResults);
            addModuleCoverage(document, testResults);
            addHealthScore(document, health, passed, failed, skipped, total);
            addCharts(document);
            addDetailedResults(document, testResults);
            if (failed > 0) addFailureAnalysis(document, testResults);
            addRecommendations(document, passed, failed, skipped, total);
            addEnvironmentInfo(document, suiteStartMs, suiteElapsedMs);

            System.out.println("[ReportManager] PDF saved: " + pdfPath);
        } catch (Exception e) {
            System.err.println("[ReportManager] PDF generation FAILED: " + e.getClass().getSimpleName()
                    + " -- " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (document != null) {
                try { document.close(); } catch (Exception ignored) {}
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PDF SECTIONS
    // ═════════════════════════════════════════════════════════════════════════

    private static void addCoverHeader(Document doc) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        Table banner = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
        banner.addCell(new Cell()
            .setBackgroundColor(C_NAVY).setPadding(18).setBorder(Border.NO_BORDER)
            .add(new Paragraph("TOSKIE WEB AUTOMATION")
                .setFont(bold).setFontSize(22).setFontColor(C_WHITE).setTextAlignment(TextAlignment.CENTER))
            .add(new Paragraph("QA Execution Report -- Management Dashboard")
                .setFont(reg).setFontSize(11).setFontColor(new DeviceRgb(180, 200, 235)).setTextAlignment(TextAlignment.CENTER))
            .add(new Paragraph("Generated: " + new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a").format(new Date()))
                .setFont(reg).setFontSize(8).setFontColor(new DeviceRgb(140, 165, 210)).setTextAlignment(TextAlignment.CENTER)));
        doc.add(banner);
        doc.add(gap());
    }

    private static void addExecutiveSummary(Document doc, long suiteStartMs,
                                             long suiteElapsedMs, double passRate) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "EXECUTIVE SUMMARY", C_GRAY);

        String execDate = suiteStartMs > 0
            ? new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a").format(new Date(suiteStartMs))
            : new SimpleDateFormat("dd MMM yyyy").format(new Date());

        Table t = new Table(UnitValue.createPercentArray(new float[]{28, 47, 25})).useAllAvailableWidth();
        t.setBorder(new SolidBorder(C_BORDER, 1));

        addInfoRow3(t, bold, reg, "Project",       "Toskie Web Automation",
                                  "Report To",      "Abhishek Satyam");
        addInfoRow3(t, bold, reg, "Suite",         "Toskie Suite (testng.xml)",
                                  "QA Tester",      "Santosh Kumar Gupta");
        addInfoRow3(t, bold, reg, "Framework",     "Playwright + Java 21",
                                  "Environment",    "DEV");
        addInfoRow3(t, bold, reg, "Browser",       ConfigManager.getBrowser().toUpperCase(),
                                  "Base URL",       ConfigManager.getBaseUrl());
        addInfoRow3(t, bold, reg, "Exec Date",     execDate,
                                  "Duration",       formatDuration(suiteElapsedMs));
        addInfoRow3(t, bold, reg, "Java Version",  System.getProperty("java.version"),
                                  "Pass Rate",      String.format("%.1f%%", passRate));
        doc.add(t);
        doc.add(gap());
    }

    private static void addDashboard(Document doc, int total, int passed, int failed,
                                      int skipped, double passRate, long elapsedMs) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "DASHBOARD", C_GRAY);

        Table t = new Table(UnitValue.createPercentArray(
                new float[]{16.6f, 16.6f, 16.6f, 16.6f, 16.6f, 16.6f})).useAllAvailableWidth();

        addDashCard(t, bold, reg, "TOTAL",     String.valueOf(total),            C_BLUE);
        addDashCard(t, bold, reg, "PASSED",    String.valueOf(passed),           C_GREEN);
        addDashCard(t, bold, reg, "FAILED",    String.valueOf(failed),           failed  > 0 ? C_RED   : C_GREEN);
        addDashCard(t, bold, reg, "SKIPPED",   String.valueOf(skipped),          skipped > 0 ? C_AMBER : C_GREEN);
        addDashCard(t, bold, reg, "PASS RATE", String.format("%.1f%%", passRate),
                    passRate >= 95 ? C_GREEN : passRate >= 75 ? C_AMBER : C_RED);
        addDashCard(t, bold, reg, "DURATION",  formatDuration(elapsedMs),       C_BLUE);
        doc.add(t);
        doc.add(gap());
    }

    private static void addClassSummary(Document doc, List<String[]> testResults) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "CLASS EXECUTION SUMMARY", C_GRAY);

        Map<String, List<String[]>> byClass = new LinkedHashMap<>();
        for (String[] r : testResults) {
            if (r.length >= 6) byClass.computeIfAbsent(r[0], k -> new ArrayList<>()).add(r);
        }

        Table t = new Table(UnitValue.createPercentArray(
                new float[]{32, 10, 10, 10, 10, 16, 12})).useAllAvailableWidth();
        for (String h : new String[]{"Class Name", "Total", "Passed", "Failed", "Skipped", "Duration", "Status"}) {
            t.addHeaderCell(hdrCell(bold, h));
        }

        boolean alt = false;
        for (Map.Entry<String, List<String[]>> e : byClass.entrySet()) {
            List<String[]> rows = e.getValue();
            int p = 0, f = 0, s = 0;
            long dur = 0;
            for (String[] r : rows) {
                if      ("PASS".equals(r[2]))    p++;
                else if ("FAIL".equals(r[2]))    f++;
                else                             s++;
                try { dur += Long.parseLong(r[5]); } catch (Exception ignored) {}
            }
            int tot = p + f + s;
            DeviceRgb bg = alt ? C_LGRAY : C_WHITE; alt = !alt;

            t.addCell(dc(reg,  e.getKey(),          bg, TextAlignment.LEFT,   C_DKGRAY));
            t.addCell(dc(bold, String.valueOf(tot),  bg, TextAlignment.CENTER, C_DKGRAY));
            t.addCell(dc(bold, String.valueOf(p),    bg, TextAlignment.CENTER, C_GREEN));
            t.addCell(dc(bold, String.valueOf(f),    bg, TextAlignment.CENTER, f > 0 ? C_RED   : C_DKGRAY));
            t.addCell(dc(bold, String.valueOf(s),    bg, TextAlignment.CENTER, s > 0 ? C_AMBER : C_DKGRAY));
            t.addCell(dc(reg,  formatDuration(dur),  bg, TextAlignment.CENTER, C_DKGRAY));

            String    statusText  = f > 0 ? "FAILED" : (s == tot ? "SKIPPED" : "PASSED");
            DeviceRgb statusColor = f > 0 ? C_RED    : (s == tot ? C_AMBER   : C_GREEN);
            t.addCell(pill(bold, statusText, statusColor));
        }
        doc.add(t);
        doc.add(gap());
    }

    private static void addModuleCoverage(Document doc, List<String[]> testResults) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "MODULE COVERAGE", C_GRAY);

        Map<String, List<String>> modules = new LinkedHashMap<>();
        modules.put("Auth / Login",     Arrays.asList("AuthLoginTests", "AuthApiTests", "RegistrationTests", "AccountRecoveryTests"));
        modules.put("Profile Creation", Arrays.asList("PersonalInfoStepTests", "BioStepTests", "SkillStepTests", "ExperienceStepTests", "QualificationStepTests", "ProjectStepTests", "GalleryStepTests", "SurveyPageTests", "ProfileCompleteStatusTests", "ProfileVisibilityTests"));
        modules.put("Dashboard",        Arrays.asList("UserProfileTests", "TalentProfileTests", "TalentProfileApiTests", "DashboardAboutTests", "DashboardCRUDTests", "UpdateProfilePhotoTests", "SkillsApiTests", "ProjectsApiTests", "GalleryApiTests", "ReviewDashboardTests", "ShareProfileTests", "OpenToConnectTests"));
        modules.put("Posts / Feed",     Arrays.asList("FeedTests", "AddPostTests", "TextPostTests", "AIPostTests", "ExploreTests", "PostDetailTests", "LikeBookmarkTests", "SavedPostTests", "ShareButtonTests", "GalleryExploreDetailTests", "SwitchProfileTests", "AddressManagementTests", "VideoPostTests"));
        modules.put("Search",           Arrays.asList("TalentSearchUITests", "TalentSearchApiTests", "TalentAddressSearchTests", "DeleteRecentSearchTests"));
        modules.put("Messaging",        Arrays.asList("MessageRequestTests", "ConversationListTests", "FetchRequestsTests", "RequestAcceptDeclineTests", "CallListingsTests", "LikeModalTests", "SimilarTalentsTests", "ValidateTalentTests"));
        modules.put("WebSocket",        Arrays.asList("WebSocketConnectionTests", "WebSocketMessageTests", "BulkMessageReadTests", "TimezoneWebSocketTests"));
        modules.put("Regression",       Arrays.asList("HomeTests", "LoginTests", "LogoutTests", "ProfileTests", "SearchTests", "BookingTests", "ChatTests", "NotificationTests", "SettingsTests"));
        modules.put("API",              Arrays.asList("APIValidationTests", "WebSocketTests", "BlogApiTests", "LandingPageApiTests"));
        modules.put("AI / Activity",    Arrays.asList("AIBioTests", "ActivityHistoryTests"));
        modules.put("Reviews",          Collections.singletonList("ReviewTests"));
        modules.put("Misc",             Arrays.asList("ChangeLocationTests", "ViewerListTests", "UserTipsTests", "PrivacyPolicyTests", "FetchContactsReviewTests", "GalleryViewApiTests", "ProjectViewApiTests", "ViewPostApiTests"));
        modules.put("Security",         Collections.singletonList("SecurityTests"));
        modules.put("Accessibility",    Collections.singletonList("AccessibilityTests"));
        modules.put("Performance",      Collections.singletonList("PerformanceTests"));
        modules.put("Subscription",     Collections.singletonList("SubscriptionTests"));
        modules.put("E2E",              Arrays.asList("EndToEndTests", "NegativeTests", "EdgeCaseTests"));
        modules.put("Smoke",            Collections.singletonList("SmokeTests"));

        Table t = new Table(UnitValue.createPercentArray(
                new float[]{18, 37, 9, 9, 9, 9, 14})).useAllAvailableWidth();
        for (String h : new String[]{"Module", "Classes", "Total", "Passed", "Failed", "Skipped", "Pass Rate"}) {
            t.addHeaderCell(hdrCell(bold, h));
        }

        boolean alt = false;
        for (Map.Entry<String, List<String>> mod : modules.entrySet()) {
            List<String> cls = mod.getValue();
            int p = 0, f = 0, s = 0;
            for (String[] r : testResults) {
                if (r.length >= 6 && cls.contains(r[0])) {
                    if      ("PASS".equals(r[2])) p++;
                    else if ("FAIL".equals(r[2])) f++;
                    else                          s++;
                }
            }
            int tot = p + f + s;
            double rate = tot > 0 ? (p * 100.0 / tot) : 0;
            DeviceRgb bg = alt ? C_LGRAY : C_WHITE; alt = !alt;

            t.addCell(dc(bold, mod.getKey(),            bg, TextAlignment.LEFT,   C_DKGRAY));
            t.addCell(dc(reg,  String.join(", ", cls),  bg, TextAlignment.LEFT,   C_DKGRAY));
            t.addCell(dc(bold, String.valueOf(tot),      bg, TextAlignment.CENTER, C_DKGRAY));
            t.addCell(dc(bold, String.valueOf(p),        bg, TextAlignment.CENTER, C_GREEN));
            t.addCell(dc(bold, String.valueOf(f),        bg, TextAlignment.CENTER, f > 0 ? C_RED   : C_DKGRAY));
            t.addCell(dc(bold, String.valueOf(s),        bg, TextAlignment.CENTER, s > 0 ? C_AMBER : C_DKGRAY));
            t.addCell(dc(bold, String.format("%.0f%%", rate), bg, TextAlignment.CENTER,
                    rate >= 95 ? C_GREEN : rate >= 75 ? C_AMBER : C_RED));
        }
        doc.add(t);
        doc.add(gap());
    }

    private static void addHealthScore(Document doc, int health, int passed, int failed,
                                        int skipped, int total) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "FRAMEWORK HEALTH SCORE", C_GRAY);

        String    statusText  = health >= 95 ? "STABLE" : health >= 75 ? "NEEDS ATTENTION" : "CRITICAL";
        DeviceRgb statusColor = health >= 95 ? C_GREEN  : health >= 75 ? C_AMBER           : C_RED;

        Table t = new Table(UnitValue.createPercentArray(new float[]{40, 60})).useAllAvailableWidth();
        t.addCell(dc(bold, "Framework Health Score", C_LGRAY, TextAlignment.LEFT, C_GRAY));
        t.addCell(new Cell().setBackgroundColor(C_LGRAY).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(6)
            .add(new Paragraph(health + "%  --  " + statusText)
                .setFont(bold).setFontSize(13).setFontColor(statusColor)));
        t.addCell(dc(bold, "Pass Rate",              C_WHITE, TextAlignment.LEFT, C_GRAY));
        t.addCell(dc(reg,  String.format("%.1f%%", total > 0 ? (passed * 100.0 / total) : 0), C_WHITE, TextAlignment.LEFT, C_DKGRAY));
        t.addCell(dc(bold, "Critical Failures",      C_LGRAY, TextAlignment.LEFT, C_GRAY));
        t.addCell(dc(bold, String.valueOf(failed),   C_LGRAY, TextAlignment.LEFT, failed > 0 ? C_RED : C_GREEN));
        t.addCell(dc(bold, "Skipped Tests",          C_WHITE, TextAlignment.LEFT, C_GRAY));
        t.addCell(dc(bold, String.valueOf(skipped),  C_WHITE, TextAlignment.LEFT, skipped > 0 ? C_AMBER : C_DKGRAY));
        doc.add(t);
        doc.add(gap());
    }

    private static void addCharts(Document doc) throws Exception {
        addSectionHeader(doc, "CHARTS & ANALYTICS", C_GRAY);

        File pie = new File(BASE + "/Reports/Charts/piechart.png");
        File bar = new File(BASE + "/Reports/Charts/barchart.png");

        if (!pie.exists() && !bar.exists()) {
            PdfFont reg = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            doc.add(new Paragraph("Charts not available -- chart files were not generated during this run.")
                .setFont(reg).setFontSize(8).setFontColor(C_DKGRAY));
            doc.add(gap());
            return;
        }

        Table t = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        if (pie.exists()) {
            t.addCell(new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .add(new Image(ImageDataFactory.create(pie.getAbsolutePath())).setWidth(248).setHeight(150)));
        } else {
            t.addCell(new Cell().setBorder(Border.NO_BORDER));
        }
        if (bar.exists()) {
            t.addCell(new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
                .add(new Image(ImageDataFactory.create(bar.getAbsolutePath())).setWidth(248).setHeight(150)));
        } else {
            t.addCell(new Cell().setBorder(Border.NO_BORDER));
        }
        doc.add(t);
        doc.add(gap());
    }

    private static void addDetailedResults(Document doc, List<String[]> testResults) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "DETAILED TEST RESULTS", C_GRAY);

        Table t = new Table(UnitValue.createPercentArray(
                new float[]{5, 27, 42, 13, 13})).useAllAvailableWidth();
        for (String h : new String[]{"#", "Class", "Method", "Status", "Duration"}) {
            t.addHeaderCell(hdrCell(bold, h));
        }

        boolean alt = false;
        int idx = 1;
        for (String[] r : testResults) {
            if (r.length < 6) continue;
            long dur = 0;
            try { dur = Long.parseLong(r[5]); } catch (Exception ignored) {}
            DeviceRgb bg = alt ? C_LGRAY : C_WHITE; alt = !alt;
            DeviceRgb sc = "PASS".equals(r[2]) ? C_GREEN : "FAIL".equals(r[2]) ? C_RED : C_AMBER;

            t.addCell(dc(reg,  String.valueOf(idx++), bg, TextAlignment.CENTER, C_DKGRAY));
            t.addCell(dc(reg,  r[0],                  bg, TextAlignment.LEFT,   C_DKGRAY));
            t.addCell(dc(reg,  r[1],                  bg, TextAlignment.LEFT,   C_DKGRAY));
            t.addCell(pill(bold, r[2], sc));
            t.addCell(dc(reg,  formatDuration(dur),   bg, TextAlignment.CENTER, C_DKGRAY));
        }
        doc.add(t);
        doc.add(gap());
    }

    private static void addFailureAnalysis(Document doc, List<String[]> testResults) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        List<String[]> failures = testResults.stream()
            .filter(r -> r.length >= 6 && "FAIL".equals(r[2]))
            .collect(Collectors.toList());
        if (failures.isEmpty()) return;

        addSectionHeader(doc, "FAILURE ANALYSIS (" + failures.size() + " failure(s))", C_RED);

        for (int i = 0; i < failures.size(); i++) {
            String[] r = failures.get(i);

            // Failure title bar
            Table fhdr = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
            fhdr.addCell(new Cell()
                .setBackgroundColor(new DeviceRgb(255, 235, 235))
                .setBorder(new SolidBorder(C_RED, 1)).setPadding(7)
                .add(new Paragraph("FAILURE #" + (i + 1) + "  |  " + r[0] + "  →  " + r[1])
                    .setFont(bold).setFontSize(9).setFontColor(C_RED)));
            doc.add(fhdr);

            Table fd = new Table(UnitValue.createPercentArray(new float[]{22, 78})).useAllAvailableWidth();
            fd.addCell(dc(bold, "Class",           C_LGRAY, TextAlignment.LEFT, C_GRAY));
            fd.addCell(dc(reg,  r[0],              C_LGRAY, TextAlignment.LEFT, C_DKGRAY));
            fd.addCell(dc(bold, "Method",          C_WHITE, TextAlignment.LEFT, C_GRAY));
            fd.addCell(dc(reg,  r[1],              C_WHITE, TextAlignment.LEFT, C_DKGRAY));
            fd.addCell(dc(bold, "Error",           C_LGRAY, TextAlignment.LEFT, C_GRAY));
            fd.addCell(dc(reg,  r[3].isEmpty() ? "N/A" : r[3], C_LGRAY, TextAlignment.LEFT, C_DKGRAY));
            fd.addCell(dc(bold, "Root Cause",      C_WHITE, TextAlignment.LEFT, C_GRAY));
            fd.addCell(dc(reg,  categorizeError(r[3]), C_WHITE, TextAlignment.LEFT, C_DKGRAY));
            fd.addCell(dc(bold, "Fix",             C_LGRAY, TextAlignment.LEFT, C_GRAY));
            fd.addCell(dc(reg,  suggestFix(r[3]),  C_LGRAY, TextAlignment.LEFT, C_DKGRAY));
            doc.add(fd);

            // Embed screenshot if present
            if (!r[4].isEmpty()) {
                File ss = new File(r[4]);
                if (ss.exists()) {
                    try {
                        doc.add(new Paragraph("Screenshot:")
                            .setFont(bold).setFontSize(8).setFontColor(C_DKGRAY).setMarginTop(4));
                        doc.add(new Image(ImageDataFactory.create(ss.toURI().toURL()))
                            .setWidth(430).setHorizontalAlignment(HorizontalAlignment.LEFT));
                    } catch (Exception ignored) {}
                }
            }
            doc.add(gap());
        }
    }

    private static void addRecommendations(Document doc, int passed, int failed,
                                            int skipped, int total) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "RECOMMENDATIONS & QA INSIGHTS", C_BLUE);

        Table t = new Table(UnitValue.createPercentArray(new float[]{4, 28, 68})).useAllAvailableWidth();
        for (String h : new String[]{"#", "Area", "Recommendation"}) {
            t.addHeaderCell(hdrCell(bold, h));
        }

        List<String[]> recs = new ArrayList<>();
        if (failed == 0 && skipped == 0) {
            recs.add(new String[]{"Coverage",          "All " + total + " active tests passing. Expand coverage to all 477 automated test methods."});
        }
        if (failed > 0) {
            recs.add(new String[]{"Critical Failures", "Investigate and fix " + failed + " failing test(s) before next deployment."});
        }
        if (skipped > 0) {
            recs.add(new String[]{"Skipped Tests",     skipped + " test(s) skipped -- check @BeforeMethod dependencies and browser launch."});
        }
        recs.add(new String[]{"Parallel Execution", "Keep parallel=\"none\" -- profile creation is resource-intensive. Upgrade CPU/network for parallelism."});
        recs.add(new String[]{"Smoke Suite",        "Run testng-smoke.xml before every deployment for rapid sanity check."});
        recs.add(new String[]{"Regression Suite",   "Extended suites (Booking, Chat, Home, Search, Settings) not yet executed -- implement and run."});
        recs.add(new String[]{"Security Suite",     "SecurityTests.java not executed -- add SQL injection, XSS, and auth bypass checks."});
        recs.add(new String[]{"Performance Suite",  "PerformanceTests.java not executed -- add page load time and API response time benchmarks."});
        recs.add(new String[]{"Negative Tests",     "NegativeTests.java not executed -- add invalid input, boundary, and error state validation."});
        recs.add(new String[]{"API Validation",     "APIValidationTests.java not executed -- validate GraphQL schema and response contracts."});

        boolean alt = false;
        int i = 1;
        for (String[] rec : recs) {
            DeviceRgb bg = alt ? C_LGRAY : C_WHITE; alt = !alt;
            t.addCell(dc(reg,  String.valueOf(i++), bg, TextAlignment.CENTER, C_DKGRAY));
            t.addCell(dc(bold, rec[0],              bg, TextAlignment.LEFT,   C_DKGRAY));
            t.addCell(dc(reg,  rec[1],              bg, TextAlignment.LEFT,   C_DKGRAY));
        }
        doc.add(t);
        doc.add(gap());
    }

    private static void addEnvironmentInfo(Document doc, long suiteStartMs,
                                            long suiteElapsedMs) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont reg  = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        addSectionHeader(doc, "ENVIRONMENT & BUILD INFORMATION", C_GRAY);

        String[][] env = {
            {"Project Name",        "Toskie Web Automation"},
            {"Framework",           "Playwright + Java 21"},
            {"Playwright Version",  "1.53.0"},
            {"Java Version",        System.getProperty("java.version")},
            {"OS",                  System.getProperty("os.name") + " " + System.getProperty("os.arch")},
            {"Machine",             System.getProperty("user.name") + "@" + getHostname()},
            {"Browser",             ConfigManager.getBrowser().toUpperCase()},
            {"Base URL",            ConfigManager.getBaseUrl()},
            {"API URL",             ConfigManager.getApiUrl()},
            {"Report To",           "Abhishek Satyam"},
            {"QA Automation Tester","Santosh Kumar Gupta"},
            {"Suite File",          "testng.xml -- Toskie Suite"},
            {"Execution Date",      suiteStartMs > 0 ? new SimpleDateFormat("dd MMM yyyy, hh:mm:ss a").format(new Date(suiteStartMs)) : "N/A"},
            {"Total Duration",      formatDuration(suiteElapsedMs)},
            {"Git Branch",          "main"},
            {"Build Tool",          "Maven 3.9.14"},
        };

        Table t = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
        t.setBorder(new SolidBorder(C_BORDER, 1));
        boolean alt = false;
        for (String[] row : env) {
            DeviceRgb bg = alt ? C_LGRAY : C_WHITE; alt = !alt;
            t.addCell(new Cell().setBackgroundColor(bg).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(6)
                .add(new Paragraph(row[0]).setFont(bold).setFontSize(9).setFontColor(C_GRAY)));
            t.addCell(new Cell().setBackgroundColor(bg).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(6)
                .add(new Paragraph(row[1] != null ? row[1] : "N/A").setFont(reg).setFontSize(9).setFontColor(C_DKGRAY)));
        }
        doc.add(t);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LAYOUT HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    private static void addSectionHeader(Document doc, String title, DeviceRgb color) throws Exception {
        PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        Table t = new Table(UnitValue.createPercentArray(new float[]{100})).useAllAvailableWidth();
        t.addCell(new Cell().setBackgroundColor(color).setPadding(8).setBorder(Border.NO_BORDER)
            .add(new Paragraph(title).setFont(bold).setFontSize(10).setFontColor(C_WHITE)));
        doc.add(t);
        doc.add(new Paragraph(" ").setFontSize(3));
    }

    private static Cell hdrCell(PdfFont bold, String text) {
        return new Cell()
            .setBackgroundColor(C_NAVY).setBorder(new SolidBorder(C_NAVY, 1)).setPadding(6)
            .add(new Paragraph(text).setFont(bold).setFontSize(8)
                .setFontColor(C_WHITE).setTextAlignment(TextAlignment.CENTER));
    }

    /** Data cell with explicit text color -- always use this for colored text. */
    private static Cell dc(PdfFont font, String text, DeviceRgb bg,
                            TextAlignment align, DeviceRgb textColor) {
        return new Cell()
            .setBackgroundColor(bg).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(5)
            .add(new Paragraph(text != null ? text : "")
                .setFont(font).setFontSize(8.5f).setFontColor(textColor).setTextAlignment(align));
    }

    private static Cell pill(PdfFont bold, String status, DeviceRgb color) {
        return new Cell()
            .setBackgroundColor(color).setBorder(Border.NO_BORDER).setPadding(5)
            .add(new Paragraph(status).setFont(bold).setFontSize(7.5f)
                .setFontColor(C_WHITE).setTextAlignment(TextAlignment.CENTER));
    }

    private static void addDashCard(Table t, PdfFont bold, PdfFont reg,
                                     String label, String value, DeviceRgb color) {
        t.addCell(new Cell()
            .setBackgroundColor(C_WHITE).setBorder(new SolidBorder(color, 2)).setPadding(10)
            .add(new Paragraph(value)
                .setFont(bold).setFontSize(22).setFontColor(color).setTextAlignment(TextAlignment.CENTER))
            .add(new Paragraph(label)
                .setFont(reg).setFontSize(7.5f).setFontColor(C_DKGRAY).setTextAlignment(TextAlignment.CENTER)));
    }

    private static void addInfoRow3(Table t, PdfFont bold, PdfFont reg,
                                     String k1, String v1, String k2, String v2) {
        DeviceRgb keyBg = C_LGRAY;
        t.addCell(new Cell().setBackgroundColor(keyBg).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(6)
            .add(new Paragraph(k1).setFont(bold).setFontSize(8.5f).setFontColor(C_GRAY)));
        t.addCell(new Cell().setBackgroundColor(C_WHITE).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(6)
            .add(new Paragraph(v1 != null ? v1 : "N/A").setFont(reg).setFontSize(8.5f).setFontColor(C_DKGRAY)));
        // k2/v2 occupy one merged visual column (split into key + val)
        t.addCell(new Cell().setBackgroundColor(keyBg).setBorder(new SolidBorder(C_BORDER, 1)).setPadding(6)
            .add(new Paragraph(k2 + ": " + (v2 != null ? v2 : "N/A"))
                .setFont(reg).setFontSize(8.5f).setFontColor(C_DKGRAY)));
    }

    private static Paragraph gap() { return new Paragraph(" ").setFontSize(4); }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITY
    // ═════════════════════════════════════════════════════════════════════════

    private static int computeHealthScore(int passed, int failed, int total) {
        if (total == 0) return 0;
        int score = (int) ((passed * 100.0) / total);
        if (failed > 0) score = Math.max(0, score - 2);
        return score;
    }

    private static String formatDuration(long ms) {
        if (ms <= 0) return "N/A";
        long secs = ms / 1000;
        long mins = secs / 60;
        long hrs  = mins / 60;
        secs = secs % 60;
        mins = mins % 60;
        if (hrs  > 0) return hrs  + "h " + mins + "m " + secs + "s";
        if (mins > 0) return mins + "m " + secs + "s";
        return secs + "s";
    }

    private static String categorizeError(String error) {
        if (error == null || error.isEmpty()) return "Unknown";
        String e = error.toLowerCase();
        if (e.contains("timeout"))                          return "Timing / Wait Issue";
        if (e.contains("locator") || e.contains("selector") || e.contains("element")) return "Locator Issue";
        if (e.contains("assertion") || e.contains("expected") || e.contains("but was")) return "Assertion Failure";
        if (e.contains("network")  || e.contains("http")   || e.contains("graphql"))  return "API / Network Issue";
        if (e.contains("playwright") || e.contains("browser"))                         return "Browser Issue";
        if (e.contains("null"))                             return "Null Reference / Data Issue";
        return "Test Logic / Framework Issue";
    }

    private static String suggestFix(String error) {
        if (error == null || error.isEmpty()) return "Check test logic and add assertions.";
        String e = error.toLowerCase();
        if (e.contains("timeout"))                          return "Increase wait timeout or add waitFor(VISIBLE) before assertion.";
        if (e.contains("locator") || e.contains("selector")) return "Update XPath/CSS selector -- UI element may have changed.";
        if (e.contains("assertion"))                        return "Verify expected vs actual values; update test data if app changed.";
        if (e.contains("network")  || e.contains("http"))  return "Check API endpoint availability and QA secret validity.";
        if (e.contains("null"))                             return "Add null guard; verify QA login and token injection before navigation.";
        return "Review stack trace and check recent application changes.";
    }

    private static String getHostname() {
        try { return java.net.InetAddress.getLocalHost().getHostName(); }
        catch (Exception e) { return "unknown"; }
    }
}
