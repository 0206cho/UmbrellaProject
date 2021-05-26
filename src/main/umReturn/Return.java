package main.umReturn;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import main.DB;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.UtilDateModel;

public class Return extends JFrame implements ActionListener {

	private JPanel panelSearch, panelInfo;
	private JLabel lblDate1, lblHyphen;
	private JButton btnSearch, btnReset;
	private Vector<String> returnColumn;
	private DefaultTableModel model;
	private JTable table;
	private UtilDateModel model1, model2;
	private JDatePanelImpl datePanel1, datePanel2;
	private JDatePickerImpl datePicker1, datePicker2;
	private Date SelectedDate1, SelectedDate2;

	public Return(String title, int width, int height) {
		setTitle(title);
		setSize(width, height);
		setLocationRelativeTo(this); // 현재 클래스에 대해서 상대적인 위치
		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dispose();	//닫으면 이 프레임만 종료

		// 검색 프레임 생성
		addSearch();

		// 테이블 생성
		addTable();

		add(panelSearch, BorderLayout.NORTH);
		add(panelInfo, BorderLayout.CENTER);
		setVisible(true);
	}

	// 검색
	private void addSearch() {

		panelSearch = new JPanel();	//패널 생성
		panelSearch.setBackground(new Color(0xFFFFFF));	//패널 배경
		panelSearch.setLayout(new FlowLayout(FlowLayout.LEFT));	//왼쪽 정렬
		panelSearch.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));	//마진 설정
		lblDate1 = new JLabel("날짜 검색 : ");	//라벨
		panelSearch.add(lblDate1);

		model1 = new UtilDateModel();	//데이터모델 객체 생성
		datePanel1 = new JDatePanelImpl(model1);	//모델 객체를 이용해 JDatePanelImpl 생성 -> 달력 생성
		datePicker1 = new JDatePickerImpl(datePanel1, new DateLabelFormatter());	
		// 생성된 달력을 텍스트필드와 ...버튼으로 나타냄 / DateLabelFormatter() 생성자를 이용해  YYYY-MM-DD로 텍스트필드에 출력되는 유형 바꿈
		panelSearch.add(datePicker1);

		lblHyphen = new JLabel(" - ");
		panelSearch.add(lblHyphen);

		model2 = new UtilDateModel();
		datePanel2 = new JDatePanelImpl(model2);
		datePicker2 = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
		panelSearch.add(datePicker2);

		btnSearch = new JButton("검색");	// 검색 버튼
		btnSearch.addActionListener(this);
		panelSearch.add(btnSearch);

		btnReset = new JButton("초기화"); // 초기화 버튼
		btnReset.addActionListener(this);
		panelSearch.add(btnReset);

	}

	// 테이블 생성
	private void addTable() {
		panelInfo = new JPanel();
		panelInfo.setBackground(new Color(0xFFFFFF));
		panelInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// 테이블 헤더 생성
		returnColumn = new Vector<String>();
		returnColumn.add("반납 코드");
		returnColumn.add("우산 코드");
		returnColumn.add("학번");
		returnColumn.add("이름");
		returnColumn.add("대여일");
		returnColumn.add("반납일");

		model = new DefaultTableModel(returnColumn, 0) {
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		//테이블 생성
		returnTable();
		
		
		table.getTableHeader().setReorderingAllowed(false); // 테이블 편집X
		table.setFillsViewportHeight(true);	// 테이블 배경색
		JTableHeader tableHeader = table.getTableHeader();	//테이블 헤더 값 가져오기
		tableHeader.setBackground(new Color(0xB2CCFF));	//가져온 테이블 헤더의 색 지정

		//스크롤 팬
		JScrollPane sc = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sc.setPreferredSize(new Dimension(860, 480));
		panelInfo.add(sc);

	}

	private void returnTable() {
		model.getDataVector().removeAllElements();	//테이블 요소 삭제
		
		String returnSelect = "select return.RETURNID, um.UMBRELLAID, st.STUDENTID, st.NAME, TO_CHAR(rental.rentaldate, \'YYYY-MM-DD\'), TO_CHAR(return.returndate, \'YYYY-MM-DD\') "
				+ " from RETURN return, RENTAL rental, STUDENT st, UMBRELLA um where return.rentalid = rental.rentalid"
				+ "  and rental.studentid = st.studentid and rental.umbrellaid = um.umbrellaid";

		ResultSet rs = DB.getResultSet(returnSelect);	
		String[] rsArr = new String[6];	//값 받아올 배열
		try {
			while (rs.next()) {
				
				for (int i = 0; i < rsArr.length; i++) {
					rsArr[i] = rs.getString(i + 1);	// 값 저장
				}

				model.addRow(rsArr);	//모델에 추가

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		table = new JTable(model);	//테이블에 추가
		table.repaint();	//새로 그리기
		
	}

	public static void main(String[] args) {
		DB.init();
		new Return("반납", 900, 600);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btnSearch) {
			
			model.getDataVector().removeAllElements();	//테이블 요소 지우기
			
			String datePattern = "yyyy-MM-dd";	//데이터 포맷 형식 지정
			SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);	//데이터 포맷 형식 지정한 객체 생성

			SelectedDate1 = (Date) datePicker1.getModel().getValue();	// 클릭된 날짜값 가져오기
			SelectedDate2 = (Date) datePicker2.getModel().getValue();	// 클릭된 날짜값 가져오기

			String returnSelect = "SELECT return.returnid, umbrella.UMBRELLAID ,student.STUDENTID , student.NAME , TO_CHAR(rental.RENTALDATE, \'YYYY-MM-DD\'), TO_CHAR(RETURN.RETURNDATE, \'YYYY-MM-DD\') "
					+ "FROM \"RETURN\" return , STUDENT student , UMBRELLA umbrella, RENTAL rental "
					+ "WHERE return.RENTALID = rental.rentalid AND rental.studentid = student.studentid AND rental.umbrellaid = umbrella.umbrellaid "
					+ "and return.RETURNDATE BETWEEN \'" + dateFormatter.format(SelectedDate1) +"\' AND \'" + dateFormatter.format(SelectedDate2) + "\'";

			System.out.println(returnSelect);
			
			
			ResultSet rs = DB.getResultSet(returnSelect);	//데이터 불러오기
			String[] rsArr = new String[6];
			try {
				while (rs.next()) {

					for (int i = 0; i < rsArr.length; i++) {
						rsArr[i] = rs.getString(i + 1);
					}

					model.addRow(rsArr);	//테이블에 추가

				}
			} catch (SQLException exception) {
				// TODO Auto-generated catch block
				exception.printStackTrace();
			}

			table = new JTable(model);
			table.repaint();	//새로 그리기

			System.out.println((dateFormatter.format(SelectedDate1)));
			System.out.println((dateFormatter.format(SelectedDate2)));
		}
		
		else if(obj == btnReset) {
			returnTable();
		}
	}
}