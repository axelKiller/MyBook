package ru.startandroid.mybook.db;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import ru.startandroid.mybook.db.DbTables.DiaryItem;
import ru.startandroid.mybook.db.DbTables.Item;
import ru.startandroid.mybook.db.DbTables.Training;
import ru.startandroid.mybook.db.DbTables.Type;
import ru.startandroid.mybook.db.DbTables.TypeAndTrain;


public class DatabaseHandler extends SQLiteOpenHelper implements IDatabaseHandler {
    private static final int DATABASE_VERSION = 21;
    private static final String DATABASE_NAME = "CrossDiery.db";
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm ");
    private Training dtrain;
    private Type type;
    private TypeAndTrain type_and_train;
    private DiaryItem diaryItem;



    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        dtrain = new Training();
        type = new Type();
        type_and_train = new TypeAndTrain();
        diaryItem = new DiaryItem();
        createTables(db);
    }

    @Override
    public void createTables(SQLiteDatabase db) {
        db.execSQL(dtrain.CREATE_TABLE);
        db.execSQL(type.CREATE_TABLE);
        db.execSQL(type_and_train.CREATE_TABLE);
        db.execSQL(diaryItem.CREATE_DIARY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("SQLite", "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + diaryItem.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + type_and_train.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + dtrain.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + type.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + diaryItem.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + type_and_train.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + dtrain.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + type.TABLE_NAME);
        createTables(db);

        db.close();
    }

    @Override
    public void addTraining(Training training) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(dtrain.ID,training.get_id());
        values.put(dtrain.NAME, training.get_name());
        values.put(dtrain.ROUNDS, training.get_rnd());
        values.put(dtrain.DESCRIP, training.get_tr_descr());
        db.insert(dtrain.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void addType(Type type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(type.ID,type.get_id_tp());
        values.put(type.NAME, type.get_name_tp());
        db.insert(type.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void addTypeandTran(TypeAndTrain typeAndTrain1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put(type_and_train.ID, typeAndTrain1.get_id_tp_tr());
        values.put(type_and_train.ID_TP, typeAndTrain1.get_id_tp());
        values.put(type_and_train.ID_TR, typeAndTrain1.get_id_tr());
        db.insert(type_and_train.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void addDiary(DiaryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String str =convToString(item.getDay());
        values.put(diaryItem.ID_TP_TR, item.get_id_tp_tr());
        values.put(diaryItem.DAY, str );
        values.put(diaryItem.STATE, item.getState());
        db.insert(diaryItem.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public Training getTraining(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(dtrain.TABLE_NAME,
                new String[]{dtrain.ID, dtrain.NAME,dtrain.ROUNDS, dtrain.DESCRIP}, dtrain.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null){
            cursor.moveToFirst();
        }

        Training training = new Training(Integer.parseInt(cursor.getString(0)), cursor.getString(1),cursor.getString(2),cursor.getString(3));

        return training;
    }



    @Override
    public Type getType(int id) {
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(type.TABLE_NAME, new String[]{type.ID, type.NAME}, type.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        Type type = new Type(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
        cursor.close();
        return type;
    }

    @Override
    public TypeAndTrain getTypeandTrain(int id) {
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(type_and_train.TABLE_NAME, new String[]{type_and_train.ID, type_and_train.ID_TP,type_and_train.ID_TR}, type_and_train.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        TypeAndTrain item = new TypeAndTrain(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)));
        cursor.close();
        return item;
    }

    @Override
    public DiaryItem getDiaryItem(int id) {
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.query(diaryItem.TABLE_NAME, new String[]{diaryItem.ID, diaryItem.ID_TP_TR, diaryItem.DAY, diaryItem.STATE}, diaryItem.ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
        DiaryItem item = new DiaryItem(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),convertToDate(cursor.getString(2)),Integer.parseInt(cursor.getString(3)));
        return item;
    }

    @Override
    public List<Type> getAllType() {
        List<Type> typeList = new ArrayList<Type>();
        String selectQuery = "SELECT  * FROM  " + type.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Type type = new Type();
                type.set_id_tp(Integer.parseInt(cursor.getString(0)));
                type.set_name_tp(cursor.getString(1));
                typeList.add(type);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return typeList;
    }

    @Override
    public Item SearchClientTraining(String sort,String  ex) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query= null;
        if (sort =="")
        {
            query = "select " + TypeAndTrain.TABLE_NAME + "." + TypeAndTrain.ID + "," + Training.TABLE_NAME + "." + Training.ID + "," + Training.TABLE_NAME + "." + Training.NAME + "," + Training.TABLE_NAME + "." + Training.DESCRIP
                    + " from " + TypeAndTrain.TABLE_NAME
                    + " inner join " + Type.TABLE_NAME + " on " + TypeAndTrain.TABLE_NAME + "." + TypeAndTrain.ID_TP + " = " + Type.TABLE_NAME + "." + Type.ID
                    + " inner join " + Training.TABLE_NAME + " on " + TypeAndTrain.TABLE_NAME + "." + TypeAndTrain.ID_TR + " = " + Training.TABLE_NAME + "." + Training.ID;
        }
        else {
            query = "select " + TypeAndTrain.TABLE_NAME + "." + TypeAndTrain.ID + "," + Training.TABLE_NAME + "." + Training.ID + "," + Training.TABLE_NAME + "." + Training.NAME + "," + Training.TABLE_NAME + "." + Training.DESCRIP
                    + " from " + TypeAndTrain.TABLE_NAME
                    + " inner join " + Type.TABLE_NAME + " on " + TypeAndTrain.TABLE_NAME + "." + TypeAndTrain.ID_TP + " = " + Type.TABLE_NAME + "." + Type.ID
                    + " inner join " + Training.TABLE_NAME + " on " + TypeAndTrain.TABLE_NAME + "." + TypeAndTrain.ID_TR + " = " + Training.TABLE_NAME + "." + Training.ID
                    + " WHERE " + Type.TABLE_NAME + "." + Type.NAME + "='" + sort + "'";
        }

        Cursor cursor = db.rawQuery(query, null);
        logCursor(cursor);
        Random rand = new Random();
        List<Item> list = new ArrayList<Item>() ;
        if (ex.compareTo("Любая" )==0) {
            if (cursor.moveToFirst()) {
                do {
                        Item item = new Item(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
                    list.add(item);
                } while (cursor.moveToNext());
            }
        }
        else {
            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(3).contains(ex)) {
                        Item item = new Item(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3));
                        list.add(item);
                    }
                } while (cursor.moveToNext());
            }
        }
        int size = list.size();
        Item it = null;
        if (size > 0) {
            it = list.get(rand.nextInt(size));
            cursor.close();
            return it;
        }
        cursor.close();
        return it;
    }

    void logCursor(Cursor c) {
        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    str = "";
                    for (String cn : c.getColumnNames()) {
                        str = str.concat(cn + ":" + c.getString(c.getColumnIndex(cn)) + "; ");
                    }
                    Log.d("myLog", str);
                } while (c.moveToNext());
            }
        } else
            Log.d("myLog", "Cursor is null");
    }



    @Override
    public List<Training> getAllTraining() {
        List<Training> trainingList = new ArrayList<Training>();
        String selectQuery = "SELECT  * FROM  " + dtrain.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Training training = new Training();
                training.set_id(Integer.parseInt(cursor.getString(0)));
                training.set_name(cursor.getString(1));
                training.set_rnd(cursor.getString(2));
                training.set_tr_descr(cursor.getString(3));
                trainingList.add(training);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return trainingList;
    }

    public Integer getMaxTypeAndTrainID()
    {
        Integer max = 0;
        String selectQuery = "SELECT  MAX("+type_and_train.TABLE_NAME+"."+type_and_train.ID  +") FROM  " + type_and_train.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor!= null)
        {
            return cursor.getInt(0)+1;
        }
        return max+1;
    }


    @Override
    public List<TypeAndTrain> getAllTypeAndTrain() {
        List<TypeAndTrain> List = new ArrayList<TypeAndTrain>();
        String selectQuery = "SELECT  * FROM " + type_and_train.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                TypeAndTrain item = new TypeAndTrain();
                item.set_id_tp_tr(Integer.parseInt(cursor.getString(0)));
                item.set_id_tp(Integer.parseInt(cursor.getString(1)));
                item.set_id_tr(Integer.parseInt(cursor.getString(2)));
                List.add(item);
            } while (cursor.moveToNext());
        }
        return List;
    }

    @Override
    public List<DiaryItem> getAllDiary() {
        List<DiaryItem> diaryItemList = new ArrayList<DiaryItem>();
        String selectQuery = "SELECT  * FROM  " + diaryItem.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String  str = cursor.getString(2);
                DiaryItem item = new DiaryItem();
                item.set_id_dr(Integer.parseInt(cursor.getString(0)));
                item.set_id_tp_tr(Integer.parseInt(cursor.getString(1)));
                item.setDay(convertToDate(str));
                item.setState(Integer.parseInt(cursor.getString(3)));
                diaryItemList.add(item);
            } while (cursor.moveToNext());
        }
        return diaryItemList;
    }

    @Override
    public List<DiaryItem> SearchDieryList(GregorianCalendar date) {
        List<DiaryItem> diaryItemList = new ArrayList<DiaryItem>();
        String selectQuery = "SELECT  * FROM  " + diaryItem.TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Date checkdate = convertToDate(cursor.getString(2));
                if(checkdate.getDay() == date.DAY_OF_MONTH &&checkdate.getMonth() == date.MONTH && checkdate.getYear() == date.YEAR ) {
                    DiaryItem item = new DiaryItem();
                    item.set_id_dr(Integer.parseInt(cursor.getString(0)));
                    item.set_id_tp_tr(Integer.parseInt(cursor.getString(1)));
                    item.setDay(convertToDate(cursor.getString(2)));
                    item.setState(Integer.parseInt(cursor.getString(3)));
                    diaryItemList.add(item);
                }
            } while (cursor.moveToNext());
        }
        return diaryItemList;
    }

    @Override
    public int updateTraining(Training contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dtrain.NAME, contact.get_name());
        values.put(dtrain.ROUNDS, contact.get_rnd());
        values.put(dtrain.DESCRIP, contact.get_tr_descr());

        return db.update(dtrain.TABLE_NAME, values, dtrain.ID + "=?",
                new String[] { String.valueOf(contact.get_id()) });
    }

    @Override
    public int updateType(Type type1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(type.NAME, type1.get_name_tp());
        return db.update(type.TABLE_NAME, values, type.ID + "=?",
                new String[] { String.valueOf(type1.get_id_tp()) });
    }

    @Override
    public int updateDiary(DiaryItem diaryItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(diaryItem.ID_TP_TR, diaryItem.get_id_tp_tr());
        values.put(diaryItem.DAY, convToString(diaryItem.getDay()) );
        values.put(diaryItem.STATE, diaryItem.getState());
        return db.update(diaryItem.TABLE_NAME, values, diaryItem.ID + "=?",
                new String[] { String.valueOf(diaryItem.get_id_dr()) });
    }

    @Override
    public int updateTypeAndTrain(TypeAndTrain type1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(type_and_train.ID, type1.get_id_tp_tr());
        values.put(type_and_train.ID_TP, type1.get_id_tp() );
        values.put(type_and_train.ID_TR, type1.get_id_tr());
        return db.update(diaryItem.TABLE_NAME, values, diaryItem.ID + "=?",
                new String[] { String.valueOf(diaryItem.get_id_dr()) });
    }

    @Override
    public void deleteTraining(Training contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(dtrain.TABLE_NAME, dtrain.ID + " =?", new String[]{String.valueOf(contact.get_id())});
        db.close();
    }

    @Override
    public void deleteTraining(Type type1) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(type.TABLE_NAME, type.ID + " =?", new String[] { String.valueOf(type1.get_id_tp())});
        db.close();
    }



    @Override
    public void deleteDiaryItem(DiaryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(diaryItem.TABLE_NAME, diaryItem.ID + " LIKE ? ", new String[] {String.valueOf(item.get_id_dr())});
        db.close();
    }

    @Override
    public void deleteTypeAndTrain(TypeAndTrain type) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(type_and_train.TABLE_NAME, type_and_train.ID + " LIKE ?", new String[] { Integer.toString(type.get_id_tp_tr())});
        db.close();
    }


    @Override
    public int getTrainingCount() {
        String countQuery = "SELECT  * FROM " + dtrain.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    @Override
    public int getTypeCount() {
        String countQuery = "SELECT  * FROM " + type.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    @Override
    public int getTypeAndTrainCount() {
        String countQuery = "SELECT  * FROM " + type_and_train.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

    @Override
    public int getDiaryCount() {
        String countQuery = "SELECT  * FROM " + diaryItem.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            //Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    Date convertToDate(String str)
    {
        try {
            return  format.parse(str);
        } catch (ParseException ex) {
            System.out.println("Not correct date");
            return new Date();
        }
    }

    public String convToString(Date date) {
        try {
         return format.format(date);
        } catch (Exception ex) {
            System.out.println("Not correct date");
            return null;
        }
    }


}

