package com.journaldev.expandablelistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity

{
    private String MY_FILE_NAME = "masterfoodlist.txt";

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    ArrayList<String> VirtualList = new ArrayList<String>();
    ArrayList<FoodEntry> FoodList = new ArrayList<FoodEntry>();
    HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        String ItemName = intent.getStringExtra("ItemName");
        String Category = intent.getStringExtra("Category");
        Integer ExpDate = intent.getIntExtra("ExpDate", 0);
        Integer Quantity = intent.getIntExtra("Quantity", 0);

        VirtualList.add(ItemName + "\t" + Category + "\t" +  ExpDate + "\t" +  Quantity);

       /* try {
            FileOutputStream fileout = openFileOutput("masterlist.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write("Chicken" + "\t" + "Meat" + "\t" + "20160430" + "\t" + "2" + "\t" + "\n");
            outputWriter.write("Pork" + "\t" + "Meat" + "\t" + "20150430" + "\t" + "2");
            outputWriter.flush();
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }*/


        readSavedData();

        parseSavedData();
        //writeSavedData();


      /* try
        {
            FileOutputStream fileout=openFileOutput("masterlist.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(ItemName + "\t" + Category + "\t" + ExpDate + "\t" + Quantity);
            outputWriter.flush();
            outputWriter.close();


        } catch (Exception e) {
            e.printStackTrace();
        }*/

        ExpandableListDataPump thing = new ExpandableListDataPump(FoodList);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = ExpandableListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });
        expandableListView.setAdapter(expandableListAdapter);
        registerForContextMenu(expandableListView);

       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent (MainActivity.this, AddItem.class);
                    startActivity(intent);
                }
            });

            /*Intent intent = getIntent();

            String ItemName = intent.getStringExtra("ItemName");
            String Category = intent.getStringExtra("Category");
            String ExpDate = intent.getStringExtra("ExpDate");
            String Quantity = intent.getStringExtra("Quantity");

            String item = ItemName + "\t" + Category + "\t" + Integer.parseInt(ExpDate) + "\t" + Integer.parseInt(Quantity);

            VirtualList.add(item);*/


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        // Show context menu for groups
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            menu.setHeaderTitle("Child");
            menu.add("Edit");
            menu.add("Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item
                .getMenuInfo();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int groupPosition = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int childPosition = ExpandableListView.getPackedPositionChild(info.packedPosition);

        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            // do someting with child'
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
            //System.out.println(item.getTitle());
            if( item.getTitle().equals("Edit") ) {
                // Edit

                String check = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);

                Toast.makeText(getApplicationContext(), check, Toast.LENGTH_SHORT).show();

                for (int i = 0; i < VirtualList.size(); i++) {

                    //expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
                    String[] lines = VirtualList.get(i).split("\t");
                    if (check.equals(lines[0]+"\t"+lines[2]))
                    {
                        Intent editIntent = new Intent(this, EditItem.class);


                        //VirtualList Name = editIntent.getStringArrayListExtra (lines[0]);
                        //ArrayList<String> category = editIntent.getStringArrayListExtra (lines[1]);
                        //ArrayList<String> Expiry = editIntent.getStringArrayListExtra (lines[2]);
                        //ArrayList<String> Qty = editIntent.getStringArrayListExtra (lines[3]);

                        editIntent.putExtra("ItemName",lines[0]);
                        editIntent.putExtra("Category",lines[1]);
                        editIntent.putExtra("ExpDate",lines[2]);
                        editIntent.putExtra("Quantity",lines[3]);

                        startActivity(editIntent);

                        Intent intent = getIntent();
                        Integer del = intent.getIntExtra("Submit", 0);


                        if (del == 1)
                        {
                            VirtualList.remove(i);
                            String text = String.format("pask %d %d", i, VirtualList.size());
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                            writeSavedData();
                            VirtualList.clear();
                            readSavedData();
                            parseSavedData();
                            expandableListDetail.get(expandableListTitle.get(groupPosition)).remove(childPosition);
                            expandableListAdapter.setNewItems(expandableListTitle, expandableListDetail);
                        }

                    }


                }

                //System.out.println(expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition));
                Toast.makeText(getApplicationContext(),"edit", Toast.LENGTH_SHORT).show();
            }
            else if( item.getTitle().equals("Delete")  ) {
                //Delete

                String check = expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);

                Toast.makeText(getApplicationContext(), check, Toast.LENGTH_SHORT).show();

                for (int i = 0; i < VirtualList.size(); i++) {

                    //expandableListDetail.get(expandableListTitle.get(groupPosition)).get(childPosition);
                    String[] lines = VirtualList.get(i).split("\t");
                   if (check.equals(lines[0]+"\t"+lines[2]))
                   {

                       VirtualList.remove(i);
                       String text = String.format("pask %d %d", i, VirtualList.size());
                       Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                       writeSavedData();
                       VirtualList.clear();
                       readSavedData();
                       parseSavedData();
                       expandableListDetail.get(expandableListTitle.get(groupPosition)).remove(childPosition);
                       expandableListAdapter.setNewItems(expandableListTitle,expandableListDetail);


                   }


                }

            }
        }

        return super.onContextItemSelected(item);
    }



    public void readSavedData() {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn = openFileInput("masterlist.txt");
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();


            while (readString != null) {
                datax.append(readString);
                VirtualList.add(readString);


                readString = buffreader.readLine();
            }

            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        //return datax.toString() ;
    }


    public void parseSavedData() {

        for (int i = 0; i < VirtualList.size(); i++) {
            String[] lines = VirtualList.get(i).split("\t");
            FoodEntry dummy = new FoodEntry(lines[0], lines[1], Integer.parseInt(lines[2]), Integer.parseInt(lines[3]));
            FoodList.add(dummy);
        }

        for (int i = 0; i < FoodList.size(); i++) {
            //String[] lines = FoodList;
            //Collections.sort(FoodList);
            Collections.sort(FoodList, new Comparator<FoodEntry>() {
                public int compare(FoodEntry food1, FoodEntry food2) {
                    return food1.getExpiryDate() - food2.getExpiryDate();
                }
            });
            //FoodEntry dummy = new FoodEntry(lines[0], lines[1], Integer.parseInt(lines[2]), Integer.parseInt(lines[3]));
            //FoodList.add(dummy);
        }

    }


    public void writeSavedData() {

        try {
            FileOutputStream fileout = openFileOutput("masterlist.txt", MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);

            for (int i = 0; i < VirtualList.size(); i++)
            {
                outputWriter.write(VirtualList.get(i));
            }

            outputWriter.flush();
            outputWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*
    public abstract class CustomComparator implements Comparator<FoodEntry> {
        @Override
        public int compare(FoodEntry o1, FoodEntry o2) {
            return o1.getExpiryDate().before(o2.getExpiryDate());
        }
    }
    */

}