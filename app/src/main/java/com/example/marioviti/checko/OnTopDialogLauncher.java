package com.example.marioviti.checko;

/**
 * Created by marioviti on 19/08/15.
 */
public interface OnTopDialogLauncher {

    /**
     * OVERVIEW: metodo di gestione del Dialog. Lancia in backgorund la eichiesta di una nuova sessione
     * se non è stata creata.
     *
     * @param type
     */
    void lauchDialog(int type);
}
