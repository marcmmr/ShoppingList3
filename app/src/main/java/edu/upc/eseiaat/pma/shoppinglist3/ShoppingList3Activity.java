package edu.upc.eseiaat.pma.shoppinglist3;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ShoppingList3Activity extends AppCompatActivity {
    private static final String FILENAME="shopping_list.txt"; //constant
    private static final int MAX_BYTES = 8000;

    private ArrayList<ShoppingItem> itemList;
    private ShoppingListAdapter adapter;

    private ListView list;
    private Button btn_add;
    private EditText edit_item;

    private void writeItemList() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);//obre un fitxer, li posemun try and catch per capturar l'error
            for (int i = 0; i < itemList.size(); i++) { //passem per tots els items
                ShoppingItem it = itemList.get(i);// afagem l'item
                String line = String.format("%s;%b\n", it.getText(), it.isChecked()); //capturem un string i un boolea
                fos.write(line.getBytes()); //ALT+ENTER per fer un altre catch
                }
                fos.close(); //tanquem fitxer
        } catch (FileNotFoundException e) {
            Log.e("marc", "writeItemList: FileNotFoundException");//mostrar un error
            Toast.makeText(this, R.string.cannot_write ,Toast.LENGTH_SHORT).show(); //creem un toast amb un text traduilble

        } catch (IOException e) {
            Log.e("marc", "writeItemList: IOException"); //erro en entrada/sortida per escriure al fitxer
            Toast.makeText(this, R.string.cannot_write ,Toast.LENGTH_SHORT).show();

        }

    }

    private void readItemList (){
        itemList=new ArrayList<>();
        try {
            FileInputStream fis = openFileInput(FILENAME);//sorrounf with try and catch
            byte [] buffer = new byte [MAX_BYTES];
            int nread = fis.read(buffer);// numero de bytes llegits
            if(nread>0){ // no més per quan estigui el fitxer ple ja sino al tenir -1 peta quan fem un clear all
                String content = new String(buffer, 0, nread); //passem el buffer a un string,el buffer comença a 0, numero de bytes que hem llegit
                String [] lines = content.split("\n");//extraiem les linies d'aqui
                for (String line : lines) { //passa per cada l'inia d'aquest array
                    String[] parts = line.split(";");
                    itemList.add(new ShoppingItem(parts[0], parts[1].equals("true"))); // el segon parametre es una comparacio de strings
                    //
                }
            }
            fis.close();

        } catch (FileNotFoundException e) {
            Log.i("marc", "readItemList: FileNotFoundException");

        } catch (IOException e) {
            Log.e("marc", "readItemList: IOException");
            Toast.makeText(this, R.string.cannot_read ,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStop() { //mentre la app esta parada...
        super.onStop();
        writeItemList(); //guardem les dades
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list2);

        list = (ListView) findViewById(R.id.list);
        btn_add = (Button) findViewById(R.id.btn_add);
        edit_item = (EditText) findViewById(R.id.edit_item);

        readItemList();

        adapter = new ShoppingListAdapter(
                this,
                R.layout.shopping_item,
                itemList
        );
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        edit_item.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                addItem();
                return true;
            }
        });


        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                itemList.get(pos).toggleChecked();
                adapter.notifyDataSetChanged();
            }
        });


        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> list, View item, int pos, long id) {
                maybeRemoveItem(pos);
                return true;
            }
        });

    }

    private void maybeRemoveItem(final int pos) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        String fmt = getResources().getString(R.string.confirm_message);
        builder.setMessage(String.format(fmt, itemList.get(pos).getText() ));
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                itemList.remove(pos);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show();


    }

    private void addItem() {
        String item_text =edit_item.getText().toString();
        if(!item_text.isEmpty()) {
            itemList.add(new ShoppingItem(item_text));
            adapter.notifyDataSetChanged();
            edit_item.setText("");
        }
        list.smoothScrollToPosition(itemList.size()-1);
    }
    @Override //per que aparegui el menu
    public boolean onCreateOptionsMenu(Menu menu) { //importem les classes
        MenuInflater inflater = getMenuInflater();//inflador de menus
        inflater.inflate(R.menu.options, menu); //el nostre menu es diu options
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear_checked:
                clearChecked();
                return true;
            case R.id.clear_all: //posem un nou cas per borrar tots els elemnts
                clearAll();
                return true; //sempre hem de tornar true al gestionar menus hem elimininat el case help...
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        builder.setMessage(R.string.confirm_clear_all);
        builder.setPositiveButton(R.string.clear_all, new DialogInterface.OnClickListener() { //new onClickListener...
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.create().show(); //que es mostri el quadre de dialeg
    }

    private void clearChecked() { //borrem els que estan marcats
        int i = 0;
        while (i < itemList.size()) { //mentre no estiguis al final
            if (itemList.get(i).isChecked()) { //si està marcat...
                itemList.remove(i);
            } else {
                i++; //no borraria el segon element perq no el mira
            }
        }
        adapter.notifyDataSetChanged();
    }

}
