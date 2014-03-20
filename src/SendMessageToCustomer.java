import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class SendMessageToCustomer{
	private ResultSet resultSet;
	
	public  void  getMobileNumber(String mob) {
		
		
		String selectMessage = "select a.sr_no,a.cust_code,a.message,b.mobile,b.phone from message_master a," +
				" customer_master b where a.status='INIT' and a.cust_code = b.custcode ";
		String updateMessageMasterQuery="update message_master set status = ? , sent_date=now() where sr_no=?";
		String sr_no,cust_code,message,mobileNumber,phoneNumber;
		String sentStatus;
		Connection connection = null;
		PreparedStatement updateStatement;
		Pattern pt = Pattern.compile("[^0-9]");
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("class loaded  ...");
			//connection = DriverManager.getConnection("jdbc:mysql://192.169.1.150:3306/js_data","dev","");
			
			//client side changes
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/js_data","root","@ss123");
			
			System.out.println(" connected to DB "+connection.toString());
			PreparedStatement preparedStatement = connection.prepareStatement(selectMessage);
			updateStatement = connection.prepareStatement(updateMessageMasterQuery);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				do{
					sr_no = resultSet.getString("sr_no");
					cust_code = resultSet.getString("cust_code");
					message = resultSet.getString("message");
					mobileNumber = resultSet.getString("mobile");
					phoneNumber = resultSet.getString("phone");

					System.out.println("cust_code  :"+cust_code);
					System.out.println("SR _NO  :"+sr_no);
					System.out.println("message   :"+message);
					System.out.println("mobile no  :"+mobileNumber);
					System.out.println("phone no  :"+phoneNumber+"\n====");
					
					if(!mob.equals(""))
						mobileNumber = mob;


					if (pt.matcher(mobileNumber).find())	
						mobileNumber = "";
					if (pt.matcher(phoneNumber).find() || phoneNumber.length() < 10)
						phoneNumber = "";
					
					if (mobileNumber.equals("") && !phoneNumber.equals(""))
						mobileNumber = phoneNumber;
						
					
					if(mobileNumber.equals("") ){
						sentStatus = "NO NUMBER";
					}
					else {
					
						
						String messageUrl="http://luna.a2wi.co.in:7501/failsafe/HttpLink?aid=512643&pin=gaint123&" +
											"mnumber="+mobileNumber.trim()+"&message=" +
											URLEncoder.encode(message) + "&signature=JANTAS";
						
						
						System.out.println("message URl :"+messageUrl);
						
						URL myURL = new URL(messageUrl);
						URLConnection myURLConnection = myURL.openConnection();
					    myURLConnection.connect();
					    
					   InputStream inp = (InputStream) myURLConnection.getInputStream();
					   sentStatus = "SENT";
					}

					updateStatement.setString(1, sentStatus);
					updateStatement.setString(2, sr_no);
					System.out.println("update "+updateMessageMasterQuery);
				    int count = updateStatement.executeUpdate();
				    if(count>0)
				    	System.out.println("mesage master updated successfully ");
	
					System.out.println("\n==============");
						
						
						
					
				}while(resultSet.next());
			}
			
			connection.close(); 
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		
				
	}

public  void  getMobileNumberOld(String mob) {
		
		
		String selectMessage = "select * from message_master where status='INIT' ";
		String mobileNumQuery = "select mobile,phone from customer_master where custcode=? ";
		Connection connection = null;
		
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("class loaded  ...");
			//connection = DriverManager.getConnection("jdbc:mysql://192.169.1.150:3306/js_data","dev","");
			
			//client side changes
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/js_data","root","@ss123");
			
			System.out.println(" connected to DB "+connection.toString());
			PreparedStatement preparedStatement = connection.prepareStatement(selectMessage);
			resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				do{
					String sr_no = resultSet.getString("sr_no");
					String cust_code = resultSet.getString("cust_code");
					String message = resultSet.getString("message");
					preparedStatement = connection.prepareStatement(mobileNumQuery);
					preparedStatement.setString(1, cust_code);
					
					ResultSet set = preparedStatement.executeQuery();
					while(set.next()){
						String mobNNUmber=null;
						
						if(!mob.equals(""))
							mobNNUmber = mob;
						else
							mobNNUmber = set.getString("mobile");
						if (mobNNUmber.matches("[^0-9]"))
							 mobNNUmber = "";
						
												
						if(mobNNUmber.equals("") && set.getString("phone").length()<10){
							 String updateErrorMessageMasterQuery="update message_master set status = ? where sr_no=?";
							    PreparedStatement updateErrorStatement = connection.prepareStatement(updateErrorMessageMasterQuery);
							    updateErrorStatement.setString(1, "NO NUMBER");
							    updateErrorStatement.setString(2, sr_no);
							    System.out.println("update "+updateErrorMessageMasterQuery);
							    int count = updateErrorStatement.executeUpdate();
							    if(count>0)
							    	System.out.println("mesage master error updated successfully ");
								System.out.println("\n==============");
							continue;
						}
						
						
						System.out.println("cust_code  :"+cust_code);
						System.out.println("SR _NO  :"+sr_no);
						System.out.println("message   :"+message);
						System.out.println("mobile no  :"+mobNNUmber+"#");
						System.out.println("phone no  :"+set.getString("phone")+"\n====");
						
						if(mobNNUmber.equals("")){
							mobNNUmber = set.getString("phone");
						}
						if (mobNNUmber.matches("[^0-9]"))
							mobNNUmber = "";
						
						String messageUrl="http://luna.a2wi.co.in:7501/failsafe/HttpLink?aid=512643&pin=gaint123&" +
											"mnumber="+mobNNUmber.trim()+"&message=" +
											URLEncoder.encode(message) + "&signature=JANTAS";
						
						
						System.out.println("message URl :"+messageUrl);
						
						URL myURL = new URL(messageUrl);
						URLConnection myURLConnection = myURL.openConnection();
					    myURLConnection.connect();
					    
					   InputStream inp = (InputStream) myURLConnection.getInputStream();
					    
					    String updateMessageMasterQuery="update message_master set status = ? , sent_date=now() where sr_no=?";
					    PreparedStatement updateStatement = connection.prepareStatement(updateMessageMasterQuery);
					    updateStatement.setString(1, "SENT");
					    updateStatement.setString(2, sr_no);
					    System.out.println("update "+updateMessageMasterQuery);
					    int count = updateStatement.executeUpdate();
					    if(count>0)
					    	System.out.println("mesage master updated successfully ");
		
						System.out.println("\n==============");
						
						
						
					}
					
				}while(resultSet.next());
			}
			
			connection.close(); 
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		
				
	}
	public static void main(String[] args) {
		
		SendMessageToCustomer send = new SendMessageToCustomer();
		//System.out.println("main "+args[0]);
		if(args.length>0)
			send.getMobileNumber(args[0]);
		else
			send.getMobileNumber("");
	}



	
	

}
