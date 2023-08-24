package com.lizhou.dao.impl;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;

import com.lizhou.bean.Clazz;
import com.lizhou.bean.Course;
import com.lizhou.bean.Exam;
import com.lizhou.bean.Grade;
import com.lizhou.bean.Page;
import com.lizhou.bean.Student;
import com.lizhou.dao.inter.BaseDaoInter;
import com.lizhou.dao.inter.ExamDaoInter;
import com.lizhou.dao.inter.StudentDaoInter;
import com.lizhou.tools.MysqlTool;

/**
 * 
 * @author bojiangzhou
 *
 */
public class ExamDaoImpl extends BaseDaoImpl implements ExamDaoInter {

	public List<Exam> getExamList(String sql, List<Object> param) {
		//鏁版嵁闆嗗悎
		List<Exam> list = new LinkedList<>();
		try {
			//鑾峰彇鏁版嵁搴撹繛鎺�
			Connection conn = MysqlTool.getConnection();
			//棰勭紪璇�
			PreparedStatement ps = conn.prepareStatement(sql);
			//璁剧疆鍙傛暟
			if(param != null && param.size() > 0){
				for(int i = 0;i < param.size();i++){
					ps.setObject(i+1, param.get(i));
				}
			}
			//鎵цsql璇彞
			ResultSet rs = ps.executeQuery();
			//鑾峰彇鍏冩暟鎹�
			ResultSetMetaData meta = rs.getMetaData();
			//閬嶅巻缁撴灉闆�
			while(rs.next()){
				//鍒涘缓瀵硅薄
				Exam exam = new Exam();
				//閬嶅巻姣忎釜瀛楁
				for(int i=1;i <= meta.getColumnCount();i++){
					String field = meta.getColumnName(i);
					Object value = rs.getObject(field);
					BeanUtils.setProperty(exam, field, rs.getObject(field));
				}
				//鏌ヨ鐝骇
				Clazz clazz = (Clazz) getObject(Clazz.class, "SELECT * FROM clazz WHERE id=?", new Object[]{exam.getClazzid()});
				//鏌ヨ骞寸骇
				Grade grade = (Grade) getObject(Grade.class, "SELECT * FROM grade WHERE id=?", new Object[]{exam.getGradeid()});
				//鏌ヨ绉戠洰
				Course course = (Course) getObject(Course.class, "SELECT * FROM course WHERE id=?", new Object[]{exam.getCourseid()});
				//娣诲姞
				exam.setClazz(clazz);
				exam.setGrade(grade);
				exam.setCourse(course);
				//娣诲姞鍒伴泦鍚�
				list.add(exam);
			}
			//鍏抽棴杩炴帴
			MysqlTool.closeConnection();
			MysqlTool.close(ps);
			MysqlTool.close(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	

}
