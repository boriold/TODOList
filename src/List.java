import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JDialog;
import java.awt.event.WindowListener;
import java.util.LinkedList;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.omg.CORBA.Current;

public class List {

	private JFrame frame;
	private JOptionPane enter;
	private LinkedList<Task> tasks;
	private JTable table;
	private int totalTime;
	private Timer timeKeep;

	private class Task {

		private String name;
		private String time;
		private int passedTime;
		private int current;
		private int previous;

		public Task(String name, String time, int previous) {
			this.setName(name);
			this.setTime(time);
			this.previous = previous;
			this.current = previous;
			setPassedTime(0);
		}

		public void updateTime(int current) {
			this.current = current;
			this.passedTime += current - previous;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public int getPassedTime() {
			return passedTime;
		}

		public void setPassedTime(int passedTime) {
			this.passedTime = passedTime;
		}

		public int getCurrent() {
			return current;
		}

		public void setCurrent(int current) {
			this.current = current;
		}

		public int getPrevious() {
			return previous;
		}

		public void setPrevious(int previous) {
			this.previous = previous;
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					List window = new List();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public List() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		tasks = new LinkedList<Task>();
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int delay = 1000;
		totalTime = 0;
		JLabel allTime = new JLabel("0:00");
		
		ActionListener listen = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				totalTime++;
				int seconds = totalTime % 60;
				int minutes = totalTime / 60;
				if (seconds < 10) {
					allTime.setText(minutes + ":0" + seconds);
				} else {
					allTime.setText(minutes + ":" + seconds);
				}
			}
		};

		timeKeep = new Timer(delay, listen);

		JButton btnAdd = new JButton("Add");

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				timeKeep.start();
			}
		});
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				timeKeep.stop();
				try {
					FileWriter fw = new FileWriter("TODOList.txt");
					BufferedWriter bw = new BufferedWriter(fw);
					for(Task t : tasks) {
						String name = t.getName();
						int time = t.getPassedTime();
						String stime = "";
						int seconds = time % 60;
						int minutes = time / 60;
						if (seconds < 10) {
							stime = (minutes + ":0" + seconds);
						} else {
							stime = (minutes + ":" + seconds);
						}
						bw.write(stime + " " + name);
						bw.newLine();
					}
					bw.close();
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});

		table = new JTable();
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "check", "name", "time" }) {
			Class[] columnTypes = new Class[] { Boolean.class, String.class, String.class };

			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		});

		DefaultTableModel tm = (DefaultTableModel) table.getModel();
		tm.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent arg0) {
				if (arg0.getColumn() == 0) {
					arg0.getFirstRow();
					if ((boolean) tm.getValueAt(arg0.getFirstRow(), arg0.getColumn())) {
						Task var = (Task) tasks.get(arg0.getFirstRow());
						((Task) tasks.get(arg0.getFirstRow())).updateTime(totalTime);
						int seconds = var.getPassedTime() % 60;
						int minutes = var.getPassedTime() / 60;
						if (seconds < 10) {
							tm.setValueAt(minutes + ":0" + seconds, arg0.getFirstRow(), 2);
						} else {
							tm.setValueAt(minutes + ":" + seconds, arg0.getFirstRow(), 2);
						}
					} else {
						Task var = (Task) tasks.get(arg0.getFirstRow());
						((Task) tasks.get(arg0.getFirstRow())).setPrevious(totalTime);
					}
				}

			}
		});

		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String enter = (String) JOptionPane.showInputDialog(frame, "Enter task:");
				tasks.add(new Task(enter, "0:00", totalTime));
				((DefaultTableModel) table.getModel()).addRow(new Object[] { Boolean.FALSE, enter, "0:00" });
			}
		});

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnStop, GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING,
						groupLayout.createSequentialGroup()
								.addComponent(table, GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(allTime)));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE).addComponent(btnAdd)
								.addComponent(btnStop).addComponent(btnStart))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(table, GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
								.addComponent(allTime))));
		frame.getContentPane().setLayout(groupLayout);

	}
}
