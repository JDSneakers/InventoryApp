package com.zybooks.johnaustininventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import java.util.ArrayList;
import java.util.List;

public class InventoryDatabase extends SQLiteOpenHelper{

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    private static InventoryDatabase mInventoryDb;

    public enum CategorySortOrder { ALPHABETIC, UPDATE_DESC, UPDATE_ASC };

    public static InventoryDatabase getInstance(Context context) {
        if (mInventoryDb == null) {
            mInventoryDb = new InventoryDatabase(context);
        }
        return mInventoryDb;
    }

    private InventoryDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class CategoryTable {
        private static final String TABLE = "categories";
        private static final String COL_NAME = "name";
        private static final String COL_UPDATE_TIME = "updated";
    }

    private static final class ItemTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_QUANTITY = "quantity";
        private static final String COL_CATEGORY = "category";
        private static final String COL_DESCRIPTION = "description";
    }

    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create categories table
        db.execSQL("create table " + CategoryTable.TABLE + " (" +
                CategoryTable.COL_NAME + " primary key, " +
                CategoryTable.COL_UPDATE_TIME + " int)");

        // Create items table with foreign key that cascade deletes
        db.execSQL("create table " + ItemTable.TABLE + " (" +
                ItemTable.COL_ID + " integer primary key autoincrement, " +
                ItemTable.COL_NAME + ", " +
                ItemTable.COL_QUANTITY + ", " +
                ItemTable.COL_DESCRIPTION + ", " +
                ItemTable.COL_CATEGORY + ", " +
                "foreign key(" + ItemTable.COL_CATEGORY + ") references " +
                CategoryTable.TABLE + "(" + CategoryTable.COL_NAME + ") on delete cascade)");

        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_USERNAME + " primary key, " +
                UserTable.COL_PASSWORD + " varchar(255))");



        // Add some Categories
        String[] categories = { "Toys", "Electronics", "Clothing" };
        for (String cat: categories) {
            Category category = new Category(cat);
            ContentValues values = new ContentValues();
            values.put(CategoryTable.COL_NAME, category.getName());
            values.put(CategoryTable.COL_UPDATE_TIME, category.getUpdateTime());
            db.insert(CategoryTable.TABLE, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + CategoryTable.TABLE);
        db.execSQL("drop table if exists " + ItemTable.TABLE);
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                db.execSQL("pragma foreign_keys = on;");
            } else {
                db.setForeignKeyConstraintsEnabled(true);
            }
        }
    }

    public List<Category> getCategories(CategorySortOrder order) {
        List<Category> categories = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String orderBy;
        switch (order) {
            case ALPHABETIC:
                orderBy = CategoryTable.COL_NAME + " collate nocase";
                break;
            case UPDATE_DESC:
                orderBy = CategoryTable.COL_UPDATE_TIME + " desc";
                break;
            default:
                orderBy = CategoryTable.COL_UPDATE_TIME + " asc";
                break;
        }

        String sql = "select * from " + CategoryTable.TABLE + " order by " + orderBy;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setName(cursor.getString(0));
                category.setUpdateTime(cursor.getLong(1));
                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return categories;
    }

    public boolean addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, username);
        values.put(UserTable.COL_PASSWORD, password);
        long id = db.insert(UserTable.TABLE, null, values);
        return id != -1;
    }



    public boolean checkUser(String username) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE Username = ?", new String[] {username});

        if (cursor.getCount() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean grantUserLogin(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + UserTable.TABLE + " WHERE Username = ? AND Password = ?", new String[] {username, password});

        if (cursor.getCount() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public boolean addCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.COL_NAME, category.getName());
        values.put(CategoryTable.COL_UPDATE_TIME, category.getUpdateTime());
        long id = db.insert(CategoryTable.TABLE, null, values);
        return id != -1;
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CategoryTable.COL_NAME, category.getName());
        values.put(CategoryTable.COL_UPDATE_TIME, category.getUpdateTime());
        db.update(CategoryTable.TABLE, values,
                CategoryTable.COL_NAME + " = ?", new String[] { category.getName() });
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(CategoryTable.TABLE,
                CategoryTable.COL_NAME + " = ?", new String[] { category.getName() });
    }

    public List<Item> getItems(String category) {
        List<Item> items = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + ItemTable.TABLE +
                " where " + ItemTable.COL_CATEGORY + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { category });
        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setQuantity(cursor.getString(2));
                item.setDescription(cursor.getString(3));
                item.setCategory(cursor.getString(4));

                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return items;
    }

    public Item getItem(long itemId) {
        Item item = null;

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + ItemTable.TABLE +
                " where " + ItemTable.COL_ID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { Float.toString(itemId) });

        if (cursor.moveToFirst()) {
            item = new Item();
            item.setId(cursor.getInt(0));
            item.setName(cursor.getString(1));
            item.setQuantity(cursor.getString(2));
            item.setDescription(cursor.getString(3));
            item.setCategory(cursor.getString(4));
        }

        return item;
    }

    public void addItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_NAME, item.getName());
        values.put(ItemTable.COL_QUANTITY, item.getQuantity());
        values.put(ItemTable.COL_DESCRIPTION, item.getDescription());
        values.put(ItemTable.COL_CATEGORY, item.getCategory());
        long itemId = db.insert(ItemTable.TABLE, null, values);
        item.setId(itemId);

        // Change update time in Categories table
        updateCategory(new Category(item.getCategory()));
    }

    public void updateItem(Item item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_ID, item.getId());
        values.put(ItemTable.COL_NAME, item.getName());
        values.put(ItemTable.COL_QUANTITY, item.getQuantity());
        values.put(ItemTable.COL_DESCRIPTION, item.getDescription());
        values.put(ItemTable.COL_CATEGORY, item.getCategory());
        db.update(ItemTable.TABLE, values,
                ItemTable.COL_ID + " = " + item.getId(), null);

        // Change update time in Categories table
        updateCategory(new Category(item.getCategory()));
    }

    public void deleteItem(long itemId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ItemTable.TABLE,
                ItemTable.COL_ID + " = " + itemId, null);
    }

}
