package data.view_model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import data.model.DataRs;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<DataRs> currData = new MutableLiveData<DataRs>();


    public void setCurData(DataRs data) {
        currData.setValue(data);
    }

    public LiveData<DataRs> getCurData() {
        return currData;
    }

}