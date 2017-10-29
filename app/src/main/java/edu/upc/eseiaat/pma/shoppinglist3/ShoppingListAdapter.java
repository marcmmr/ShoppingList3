package edu.upc.eseiaat.pma.shoppinglist3;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by marc.montserrat.robl on 23/10/2017.
 */
public class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> { //<string>

    public ShoppingListAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if (result == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.shopping_item, null);
        }
        CheckBox checkbox = (CheckBox) result.findViewById(R.id.checkBox);
        //CheckBox checkbox = (CheckBox) result.findViewById(R.id.shopping_item);
        TextView textView = (TextView) result.findViewById(R.id.textView);
        ShoppingItem item = getItem(position); //String item_text ...
        //shopping_item.setText(item_text);
        textView.setText(item.getText()); //checkbox...
        checkbox.setChecked(item.isChecked());
        return result;
    }
}