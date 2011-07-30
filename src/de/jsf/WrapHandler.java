package de.jsf;  

import java.io.ByteArrayOutputStream;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


@ManagedBean
@SessionScoped
public class WrapHandler {
	private DataModel<Wrap> wraps;
	private Wrap aktuellerWrap;
	private boolean update;

	public WrapHandler() {
		Logger.getAnonymousLogger().log(Level.INFO, "WrapHandler()");
		wraps = new ListDataModel<Wrap>();
		wraps.setWrappedData(query());
	}

	public DataModel<Wrap> getWraps() {
		return wraps;
	}

	public Wrap getAktuellerWrap() {
		return aktuellerWrap;
	}

	public void setAktuellerWrap(Wrap aktuellerWrap) {
		this.aktuellerWrap = aktuellerWrap;
	}

	public String refresh() {
		Logger.getAnonymousLogger().log(Level.INFO, "refresh()");
		wraps.setWrappedData(query());
		return null;
	}

	public String speichern() {
		if (update) {
			Logger.getAnonymousLogger().log(Level.INFO,
					"update(): " + aktuellerWrap);
			update();
		} else {
			Logger.getAnonymousLogger().log(Level.INFO,
					"insert(): " + aktuellerWrap);
			insert();
			refresh();
		}
		return "/anzeige-wraps.xhtml";
	}

	public String aendern() {
		update = true;
		aktuellerWrap = wraps.getRowData();
		Logger.getAnonymousLogger().log(Level.INFO,
				"aendern(): " + aktuellerWrap);
		return "/wrap.xhtml";
	}

	public String neuanlage() {
		update = false;
		aktuellerWrap = new Wrap("", "", "", "");
		Logger.getAnonymousLogger().log(Level.INFO, "neuanlage()");
		return "/wrap.xhtml";
	}

	public String back() {
		Logger.getAnonymousLogger().log(Level.INFO, "back()");
		return "/anzeige-wraps.xhtml";
	}

	public String loeschen() {
		aktuellerWrap = wraps.getRowData();
		Logger.getAnonymousLogger().log(Level.INFO,
				"loeschen(): " + aktuellerWrap);
		delete();
		refresh();
		return null;
	}

	public void exportPdf() {
		Logger.getAnonymousLogger().log(Level.INFO, "exportPdf()");
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter.getInstance(document, baos);
			Font defaultFont = new Font(FontFamily.HELVETICA, 10);
			Font headerFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD);
			document.open();
			
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			document.add(new Paragraph(format.format(new Date()), defaultFont));
			PdfPTable table = new PdfPTable(new float[] { 2f, 2f, 1f });
			table.setWidthPercentage(60f);
			table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
			table.setSpacingBefore(20f);
			
			PdfPCell cell = new PdfPCell(new Phrase("Zutat1", headerFont));
			cell.setPadding(4f);
			cell.setGrayFill(0.9f);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Zutat2", headerFont));
			cell.setPadding(4f);
			cell.setGrayFill(0.9f);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Zutat3", headerFont));
			cell.setPadding(4f);
			cell.setGrayFill(0.9f);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Zutat4", headerFont));
			cell.setPadding(4f);
			cell.setGrayFill(0.9f);
			table.addCell(cell);
			
			for (Wrap w : wraps) {
				cell = new PdfPCell(new Phrase(w.getZutat1(), defaultFont));
				cell.setPadding(4f);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(w.getZutat2(), defaultFont));
				cell.setPadding(4f);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(w.getZutat3(), defaultFont));
				cell.setPadding(4f);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(w.getZutat4(), defaultFont));
				cell.setPadding(4f);
				table.addCell(cell);
			}
			
			document.add(table);
			document.close();
			
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) context
					.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setContentLength(baos.size());
			response.setHeader("Content-disposition",
					"attachment;filename=\"wraps.pdf\"");
			OutputStream out = response.getOutputStream();
			baos.writeTo(out);
			out.flush();
			response.flushBuffer();
			context.responseComplete();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"exportPdf(): " + e.getMessage());
		}
	}

	private List<Wrap> query() {
		Connection con = null;
		PreparedStatement stmt = null;
		List<Wrap> liste = new ArrayList<Wrap>();
		try {
			con = getConnection();
			stmt = con
					.prepareStatement("select wrapid, zutat1, zutat2, zutat3, zutat4 from wraps");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Wrap w = new Wrap();
				w.setWrapId(rs.getInt(1));
				w.setZutat1(rs.getString(2));
				w.setZutat2(rs.getString(3));
				w.setZutat3(rs.getString(4));
				w.setZutat4(rs.getString(5));
				liste.add(w);
			}
			rs.close();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"query(): " + e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}

		return liste;
	}

	private void update() {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = getConnection();
			stmt = con
					.prepareStatement("update wraps set zutat1 = ?, zutat2 = ?, zutat3 = ?, zutat4 =?, where wrapid = ?");

			stmt.setString(1, aktuellerWrap.getZutat1());
			stmt.setString(2, aktuellerWrap.getZutat2());
			stmt.setString(3, aktuellerWrap.getZutat3());
			stmt.setString(4, aktuellerWrap.getZutat4());
			stmt.setInt(5, aktuellerWrap.getWrapId());

			stmt.executeUpdate();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"update(): " + e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
	}

	private void delete() {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = getConnection();
			stmt = con.prepareStatement("delete from wraps where wrapid = ?");

			stmt.setInt(1, aktuellerWrap.getWrapId());

			stmt.executeUpdate();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"delete(): " + e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
	}

	private void insert() {
		Connection con = null;
		PreparedStatement stmt = null;

		try {
			con = getConnection();
			stmt = con
					.prepareStatement("insert into wraps (zutat1, zutat2, zutat3, zutat4) values (?,?,?,?)");

			stmt.setString(1, aktuellerWrap.getZutat1());
			stmt.setString(2, aktuellerWrap.getZutat2());
			stmt.setString(3, aktuellerWrap.getZutat3());
			stmt.setString(4, aktuellerWrap.getZutat4());


			stmt.executeUpdate();
		} catch (Exception e) {
			Logger.getAnonymousLogger().log(Level.SEVERE,
					"insert(): " + e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
			}
		}
	}

	private Connection getConnection() throws NamingException, SQLException {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("jdbc/__default");
		return ds.getConnection();
	}
}