package id.rwp.tugasakhir.mobileFluorescenceSpectroscopy.server;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ranuwp on 10/5/2017.
 */

/**
 * Kelas yang memberikan service untuk melakukan request ke server
 */
public class MoFluSService {

    private static final String MOFLUS_API_URL = "http://172.104.188.209/";

    /**
     * Mendapatkan service untuk melakukan komunikasi ke server
     * @return
     */
    public static IMoFluS getService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOFLUS_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(IMoFluS.class);
    }

}
