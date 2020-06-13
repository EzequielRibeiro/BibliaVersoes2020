package com.projeto.biblianvi.BibliaVersoes;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import java.util.List;


public class DetailsFragment extends Fragment {
	
	private int mIndex = 0;
    private GridView gridview;


    public static DetailsFragment newInstance(int index) {
        Log.v(MainActivityFragment.TAG, "in DetailsFragment newInstance(" + index + ")");

        DetailsFragment df = new DetailsFragment();

        // Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("index", index);
		df.setArguments(args);
		return df;
	}
	
	public static DetailsFragment newInstance(Bundle bundle) {
		int index = bundle.getInt("index", 0);
        return newInstance(index);
	}

    @Override
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {

		Log.v(MainActivityFragment.TAG,
    			"in DetailsFragment onInflate. AttributeSet contains:");
    	for(int i=0; i<attrs.getAttributeCount(); i++)
            Log.v(MainActivityFragment.TAG, "    " + attrs.getAttributeName(i) +
            		" = " + attrs.getAttributeValue(i));
    	super.onInflate(activity, attrs, savedInstanceState);

      }

	@Override
    public void onAttach(Activity myActivity) {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onAttach; activity is: " +
    			myActivity);
    	super.onAttach(myActivity);
    }

    @Override
    public void onCreate(Bundle myBundle) {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onCreate. Bundle contains:");
    	if(myBundle != null) {
            for(String key : myBundle.keySet()) {
                Log.v(MainActivityFragment.TAG, "    " + key);
            }
    	}
    	else {
            Log.v(MainActivityFragment.TAG, "    myBundle is null");
    	}
    	super.onCreate(myBundle);

    	mIndex = getArguments().getInt("index", 0);

    }

    public int getShownIndex() {
    	return mIndex;
    }

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
        Log.v(MainActivityFragment.TAG, "in DetailsFragment onCreateView. container = " +
        		container);

        // Don't tie this fragment to anything through the inflater. Android
        // takes care of attaching fragments for us. The container is only
        // passed in so we can know about the container where this View
        // hierarchy is going to go. If we're not going anywhere, don't
        // bother to create the view hierarchy and just return null.
        if(container == null) {
        	Log.v(MainActivityFragment.TAG, "container is null. No need to inflate.");
        	return null;
        }


        View v = inflater.inflate(R.layout.details, container, false);
		/*
		TextView text1 = (TextView) v.findViewById(R.id.text1);
		text1.setText(Shakespeare.DIALOGUE[ mIndex ] );
		*/

        BibliaBancoDadosHelper bibliaHelp = new BibliaBancoDadosHelper(getActivity().getApplicationContext());
        List<Biblia> bookNameList = bibliaHelp.getAllBooksName();
		//String livro[] = getResources().getStringArray(R.array.bibliaLivEp_arrays);
        String[] livro = new String[bookNameList.size()];

        for(int i = 0 ;i <= bookNameList.size()-1; i++){
            livro[i] = bookNameList.get(i).getBooksName();
        }

        int capitulos = bibliaHelp.getQuantidadeCapitulos(livro[mIndex]) ;

        Biblia biblia = new Biblia();
        biblia.setBooksName(livro[mIndex]);

        gridview = v.findViewById(R.id.gridview);
		gridview.setAdapter(new CapituloGridView(getActivity(),capitulos,biblia));

		/*
		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
									int position, long id) {
				Log.v("Capitulo GridView ", Integer.toString(position));
			}
			});
			*/

				return v;
	}


    @Override
    public void onViewCreated(View view, Bundle icicle) {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onViewCreated for View: " + view);
    	super.onViewCreated(view, icicle);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
    	Log.v(MainActivityFragment.TAG,
    			"in DetailsFragment onActivityCreated. savedState contains:");
    	if(savedState != null) {
            for(String key : savedState.keySet()) {
                Log.v(MainActivityFragment.TAG, "    " + key);
            }
    	}
    	else {
            Log.v(MainActivityFragment.TAG, "    savedState is null");
    	}
        super.onActivityCreated(savedState);
    }

    @Override
    public void onViewStateRestored(Bundle icicle) {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onViewStateRestored for view hierarchy");
    	super.onViewStateRestored(icicle);
    }

    @Override
    public void onStart() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onStart");
    	super.onStart();
    }

    @Override
    public void onResume() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onResume");
    	super.onResume();
    }

    @Override
    public void onPause() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onPause");
    	super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onSaveInstanceState. Bundle is " + icicle);
        super.onSaveInstanceState(icicle);
    	if(icicle != null) {
    		Log.v(MainActivityFragment.TAG, "icicle contains the following:");
            for(String key : icicle.keySet()) {
                Log.v(MainActivityFragment.TAG, "    " + key);
            }
    	}
    }

    @Override
    public void onStop() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onStop");
    	super.onStop();
    }

    @Override
    public void onDestroyView() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onDestroyView, view = " +
    			getView());
    	super.onDestroyView();
    }

    @Override
    public void onDestroy() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onDestroy");
    	super.onDestroy();
    }

    @Override
    public void onDetach() {
    	Log.v(MainActivityFragment.TAG, "in DetailsFragment onDetach");
    	super.onDetach();
    }
}