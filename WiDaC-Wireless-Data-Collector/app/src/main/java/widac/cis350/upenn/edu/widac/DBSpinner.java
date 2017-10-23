// Spinner for Database values
// @author: ashutosh
package widac.cis350.upenn.edu.widac;
import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
public class DBSpinner extends AppCompatSpinner
{
    OnItemSelectedListener listener;
    private AdapterView<?> lastParent;
    private View lastView;
    private long lastId;
    /**
     * Constructor
     * @param context - the current app context
     * @param attrs - context attributes
     */
    public DBSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initlistner();
    }

    /**
     * Select an item
     * @param position - the position in the wheel
     */
    @Override
    public void setSelection(int position)
    {
        if (position == getSelectedItemPosition() && listener != null)
        {
            listener.onItemSelected(lastParent, lastView, position, lastId);
        }
        else
        {
            super.setSelection(position);
        }
    }

    /**
     * Select an item from the spinner
     * @param listener - the item selected listener
     */
    public void setOnItemSelectedEvenIfUnchangedListener(OnItemSelectedListener listener)
    {
        this.listener = listener;
    }

    /**
     * Lisener for initializing the database
     */
    private void initlistner()
    {
        super.setOnItemSelectedListener(new OnItemSelectedListener() {
            /**
             * An item was selected
             * @param parent - the parent container
             * @param view - the view to draw into
             * @param position - the position of the selected element
             * @param id - the id of the element selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                lastParent = parent;
                lastView = view;
                lastId = id;
                if (listener != null)
                {
                    listener.onItemSelected(parent, view, position, id);
                }
            }

            /**
             * Nothing was selected
             * @param parent - the container view
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                if (listener != null)
                {
                    listener.onNothingSelected(parent);
                }
            }
        });
    }
}