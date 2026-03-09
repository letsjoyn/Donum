package com.ngo.servlet;

import com.ngo.model.User;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/generateReceipt")
public class ReceiptServlet extends HttpServlet {

    private static final BaseColor PRIMARY = new BaseColor(0, 210, 255);
    private static final BaseColor DARK_BG = new BaseColor(15, 23, 42);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String donationId = request.getParameter("id");
        String amount = request.getParameter("amount");
        String type = request.getParameter("type");

        if (donationId == null || amount == null || type == null) {
            response.sendRedirect("donor-dashboard.jsp?error=missing_params");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=Donum_Receipt_" + donationId + ".pdf");

        try {
            Document doc = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, response.getOutputStream());
            doc.open();

            // Fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, PRIMARY);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.DARK_GRAY);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.GRAY);
            Font valueFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.BLACK);

            // Header
            Paragraph title = new Paragraph("Donum", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            doc.add(title);

            Paragraph subtitle = new Paragraph("NGO Relief & Distribution Platform", headerFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(5);
            doc.add(subtitle);

            // Divider
            LineSeparator line = new LineSeparator();
            line.setLineColor(PRIMARY);
            line.setLineWidth(2f);
            doc.add(new Chunk(line));
            doc.add(Chunk.NEWLINE);

            // Receipt Title
            Paragraph receiptTitle = new Paragraph("OFFICIAL DONATION RECEIPT", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
            receiptTitle.setAlignment(Element.ALIGN_CENTER);
            receiptTitle.setSpacingAfter(20);
            doc.add(receiptTitle);

            // Details Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(90);
            table.setWidths(new float[]{1, 2});
            table.setSpacingBefore(10);

            addRow(table, "Receipt No:", "REC-UA-" + donationId, headerFont, valueFont);
            addRow(table, "Date:", new SimpleDateFormat("dd MMMM yyyy, hh:mm a").format(new Date()), headerFont, valueFont);
            addRow(table, "Donor Name:", user.getFullName(), headerFont, valueFont);
            addRow(table, "Donor Email:", user.getEmail(), headerFont, valueFont);
            addRow(table, "Donation Type:", type, headerFont, valueFont);
            addRow(table, "Amount/Qty:", ("Cash".equals(type) ? "INR " : "") + amount, headerFont, valueFont);
            addRow(table, "Status:", "Received & Acknowledged", headerFont, valueFont);

            doc.add(table);
            doc.add(Chunk.NEWLINE);

            // Divider
            doc.add(new Chunk(line));
            doc.add(Chunk.NEWLINE);

            // Tax notice
            Paragraph taxNote = new Paragraph(
                "This receipt is issued for tax deduction purposes under Section 80G of the Income Tax Act. " +
                "Donum Foundation is a registered non-profit organization.", normalFont);
            taxNote.setAlignment(Element.ALIGN_CENTER);
            taxNote.setSpacingAfter(15);
            doc.add(taxNote);

            Paragraph thanks = new Paragraph("Thank you for your generous contribution towards making the world a better place.", 
                new Font(Font.FontFamily.HELVETICA, 11, Font.BOLDITALIC, PRIMARY));
            thanks.setAlignment(Element.ALIGN_CENTER);
            doc.add(thanks);

            doc.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Generated by Donum | donum.org | This is a computer-generated receipt.", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);

            doc.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(8);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(8);
        table.addCell(valueCell);
    }
}
