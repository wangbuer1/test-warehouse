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
import com.lizhou.bean.CourseItem;
import com.lizhou.bean.Grade;
import com.lizhou.bean.Page;
import com.lizhou.bean.Student;
import com.lizhou.bean.Teacher;
import com.lizhou.dao.inter.BaseDaoInter;
import com.lizhou.dao.inter.StudentDaoInter;
import com.lizhou.dao.inter.TeacherDaoInter;
import com.lizhou.tools.MysqlTool;

/**
 * 鏁欏笀鏁版嵁灞�
 * @author bojiangzhou
 *
 */
public class TeacherDaoImpl extends BaseDaoImpl implements TeacherDaoInter {

	public List<Teacher> getTeacherList(String sql, Object[] param, Grade grade, Clazz clazz) {
		//鏁版嵁闆嗗悎
		List<Teacher> list = new LinkedList<>();
		try {
			//鑾峰彇鏁版嵁搴撹繛鎺�
			Connection conn = MysqlTool.getConnection();
			//棰勭紪璇�
			PreparedStatement ps = conn.prepareStatement(sql);
			//璁剧疆鍙傛暟
			if(param != null && param.length > 0){
				for(int i = 0;i < param.length;i++){
					ps.setObject(i+1, param[i]);
				}
			}
			//鎵цsql璇彞
			ResultSet rs = ps.executeQuery();
			//鑾峰彇鍏冩暟鎹�
			ResultSetMetaData meta = rs.getMetaData();
			//閬嶅巻缁撴灉闆�
			while(rs.next()){
				//鍒涘缓瀵硅薄
				Teacher teacher = new Teacher();
				//閬嶅巻姣忎釜瀛楁
				for(int i=1;i <= meta.getColumnCount();i++){
					String field = meta.getColumnName(i);
					BeanUtils.setProperty(teacher, field, rs.getObject(field));
				}
				
				List<Object> itemParam = new LinkedList<>();
				StringBuffer itemSql = new StringBuffer("SELECT * FROM clazz_course_teacher WHERE teacherid=? ");
				itemParam.add(teacher.getId());
				if(grade != null){
					itemSql.append(" AND gradeid=?");
					itemParam.add(grade.getId());
				}
				if(clazz != null){
					itemSql.append(" AND clazzid=?");
					itemParam.add(clazz.getId());
				}
				
				List<Object> objList = getList(CourseItem.class, itemSql.toString(), itemParam);
				List<CourseItem> itemList = new LinkedList<>();
				for(Object obj : objList){
					CourseItem item = (CourseItem) obj;
					//鏌ヨ鐝骇
					Clazz sclazz = (Clazz) getObject(Clazz.class, "SELECT * FROM clazz WHERE id=?", new Object[]{item.getClazzid()});
					Grade sgrade = (Grade) getObject(Grade.class, "SELECT * FROM grade WHERE id=?", new Object[]{item.getGradeid()});
					Course course = (Course) getObject(Course.class, "SELECT * FROM course WHERE id=?", new Object[]{item.getCourseid()});
					
					item.setClazz(sclazz);
					item.setGrade(sgrade);
					item.setCourse(course);
					
					itemList.add(item);
				}
				//娣诲姞
				teacher.setCourseList(itemList);
				//娣诲姞鍒伴泦鍚�
				list.add(teacher);
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
