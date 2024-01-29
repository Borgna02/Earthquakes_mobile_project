package it.danieleborgna.earthquakes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import it.danieleborgna.earthquakes.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater()); //posso fare getLayoutInflater perché sono in un'activity e non in un frammento

        setContentView(binding.getRoot());

        NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        if (fragment != null) {
            NavController controller = fragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNavigationView, controller);
        }
    }
}