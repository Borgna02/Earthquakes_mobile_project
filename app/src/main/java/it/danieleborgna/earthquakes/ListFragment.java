package it.danieleborgna.earthquakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.danieleborgna.earthquakes.databinding.FragmentListBinding;
import it.danieleborgna.earthquakes.model.Earthquake;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;

    private Boolean menuExpanded = false;
    // recupero le animazioni

    private CurrentSort currentSort = CurrentSort.DATE_DESC;

    private enum CurrentSort {
        MAGNITUDE_DESC,
        MAGNITUDE_ASC,
        DATE_DESC,
        DATE_ASC
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainViewModel mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), earthquakes -> binding.recyclerView.setAdapter(new EarthquakeAdapter(earthquakes)));


        currentSort = CurrentSort.DATE_DESC;
        setSortButtonsText();


        binding.expandMenuButton.setOnClickListener(v -> onExpandMenuClicked());

        binding.sortByDateButton.setOnClickListener(v -> {
            if (currentSort != CurrentSort.DATE_DESC) {
                currentSort = CurrentSort.DATE_DESC;
                // Nella SELECT che abbiamo definito i terremoti vengono già restituiti in ordine di data crescente
                mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), earthquakes -> binding.recyclerView.setAdapter(new EarthquakeAdapter(earthquakes)));

            } else {

                currentSort = CurrentSort.DATE_ASC;
                Comparator<Earthquake> dateComparator = Comparator.comparing(Earthquake::getDate);
                mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), earthquakes -> {
                    // Creazione di una nuova lista ordinata
                    List<Earthquake> sortedEarthquakes = new ArrayList<>(earthquakes);
                    sortedEarthquakes.sort(dateComparator);

                    // Impostazione dell'adapter con la nuova lista ordinata
                    binding.recyclerView.setAdapter(new EarthquakeAdapter(sortedEarthquakes));
                });
            }
            setSortButtonsText();
        });

        binding.sortByMagnitudeButton.setOnClickListener(v -> {
            if (currentSort != CurrentSort.MAGNITUDE_DESC) {
                currentSort = CurrentSort.MAGNITUDE_DESC;
                Comparator<Earthquake> magnitudeComparator = Comparator.comparing(Earthquake::getMagnitude);
                mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), earthquakes -> {
                    // Creazione di una nuova lista ordinata in ordine crescente
                    List<Earthquake> sortedEarthquakes = new ArrayList<>(earthquakes);
                    sortedEarthquakes.sort(magnitudeComparator);

                    // Creazione di una nuova lista ordinata in ordine decrescente (invertendo l'ordine)
                    List<Earthquake> sortedEarthquakesDescending = new ArrayList<>(sortedEarthquakes);
                    sortedEarthquakesDescending.sort(Collections.reverseOrder(magnitudeComparator));

                    // Impostazione dell'adapter con la nuova lista ordinata in ordine decrescente
                    binding.recyclerView.setAdapter(new EarthquakeAdapter(sortedEarthquakesDescending));
                });

            } else {
                currentSort = CurrentSort.MAGNITUDE_ASC;
                Comparator<Earthquake> magnitudeComparator = Comparator.comparing(Earthquake::getMagnitude);
                mainViewModel.getEarthquakes().observe(getViewLifecycleOwner(), earthquakes -> {
                    // Creazione di una nuova lista ordinata
                    List<Earthquake> sortedEarthquakes = new ArrayList<>(earthquakes);
                    sortedEarthquakes.sort(magnitudeComparator);

                    // Impostazione dell'adapter con la nuova lista ordinata
                    binding.recyclerView.setAdapter(new EarthquakeAdapter(sortedEarthquakes));
                });

            }
            setSortButtonsText();
        });


    }

    private void onExpandMenuClicked() {
        if (!menuExpanded) {
            // abilito i due tasti
            binding.sortByDateButton.setEnabled(true);
            binding.sortByMagnitudeButton.setEnabled(true);

            // eseguo le animazioni
            binding.sortByMagnitudeButton.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim));
            binding.sortByDateButton.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim));
            binding.expandMenuButton.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim));
        } else {

            // disabilito i due tasti
            binding.sortByDateButton.setEnabled(false);
            binding.sortByMagnitudeButton.setEnabled(false);

            // eseguo l'animazione
            binding.expandMenuButton.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim));

        }


        menuExpanded = !menuExpanded;
    }

    private void setSortButtonsText() {
        System.out.println(currentSort);
        switch (currentSort) {
            case DATE_DESC:
                binding.sortByDateButton.setText(R.string.sort_by_date_ascending);
                binding.sortByMagnitudeButton.setText(R.string.sort_by_magnitude_descending);
                break;
            case DATE_ASC:
                binding.sortByDateButton.setText(R.string.sort_by_date_descending);
                binding.sortByMagnitudeButton.setText(R.string.sort_by_magnitude_descending);
                break;
            case MAGNITUDE_ASC:
                binding.sortByMagnitudeButton.setText(R.string.sort_by_magnitude_descending);
                binding.sortByDateButton.setText(R.string.sort_by_date_descending);
                break;
            case MAGNITUDE_DESC:
                binding.sortByMagnitudeButton.setText(R.string.sort_by_magnitude_ascending);
                binding.sortByDateButton.setText(R.string.sort_by_date_descending);
        }
    }

}
