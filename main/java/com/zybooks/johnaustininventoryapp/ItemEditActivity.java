package com.zybooks.johnaustininventoryapp;

import android.content.Intent;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ItemEditActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "com.zybooks.johnaustininventoryapp.item_id";
    public static final String EXTRA_CATEGORY = "com.zybooks.johnaustininventoryapp.category";

    private EditText mItemText;
    private EditText mQuantityText;
    private EditText mDescriptionText;

    private InventoryDatabase mInventoryDb;
    private long mItemId;
    private Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_edit);

        mItemText = findViewById(R.id.itemText);
        mQuantityText = findViewById(R.id.quantityText);
        mDescriptionText = findViewById(R.id.descriptionText);

        mInventoryDb = InventoryDatabase.getInstance(getApplicationContext());

        // Get item ID from ItemActivity
        Intent intent = getIntent();
        mItemId = intent.getLongExtra(EXTRA_ITEM_ID, -1);

        ActionBar actionBar = getSupportActionBar();

        if (mItemId == -1) {
            // Add new Item
            mItem = new Item();
            setTitle(R.string.add_item);
        }
        else {
            // Update existing Item
            mItem = mInventoryDb.getItem(mItemId);
            mItemText.setText(mItem.getName());
            mQuantityText.setText(mItem.getQuantity());
            mDescriptionText.setText(mItem.getDescription());
            setTitle(R.string.update_item);
        }

        String category = intent.getStringExtra(EXTRA_CATEGORY);
        mItem.setCategory(category);
    }

    public void saveButtonClick(View view) {

        mItem.setName(mItemText.getText().toString());
        mItem.setQuantity(mQuantityText.getText().toString());
        mItem.setDescription(mDescriptionText.getText().toString());

        if (mItemId == -1) {
            // New Item
            mInventoryDb.addItem(mItem);
        } else {
            // Existing Item
            mInventoryDb.updateItem(mItem);
        }

        // Send back Item ID
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ITEM_ID, mItem.getId());
        setResult(RESULT_OK, intent);
        finish();
    }
}