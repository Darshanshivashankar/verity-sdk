package com.evernym.verity.sdk.wallet;

import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.wallet.WalletExistsException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Static Utilities for creating local wallets. Indy-sdk don't provide a way to check if a wallet exists so
 * these utils encapsulate creating a wallet and trapping the error when the wallet already exists.
 *
 */
public class WalletUtil {
    private WalletUtil() {}

    /**
     * Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
     * error is trapped and this function will complete but other errors will be raised.
     * @param walletName the given name for the wallet to be created
     * @param walletKey the given key for the wallet to be created
     * @throws WalletException when the creation of the wallet fails for other reasons besides that the wallet already
     * exists.
     */
    public static void tryCreateWallet(String walletName, String walletKey) throws WalletException {
        String walletConfig = new JSONObject().put("id", walletName).toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        tryToCreateWallet(walletConfig, walletCredentials);
    }

    /**
     * Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
     * error is trapped and this function will complete but other errors will be raised.
     * @param walletName the given name for the wallet to be created
     * @param walletKey the given key for the wallet to be created
     * @param walletPath the given path where the wallet on disk file will be created
     * @throws WalletException when the creation of the wallet fails for other reasons besides that the wallet already
     * exists.
     */
    public static void tryCreateWallet(String walletName, String walletKey, String walletPath) throws WalletException {
        String walletConfig = new JSONObject()
                .put("id", walletName)
                .put("path", walletPath)
                .toString();
        String walletCredentials = new JSONObject().put("key", walletKey).toString();
        tryToCreateWallet(walletConfig, walletCredentials);
    }

    /**
     * Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
     * error is trapped and this function will complete but other errors will be raised.
     *
     * @param config the WalletConfig object that defines configuration for the creation of the wallet
     * @throws WalletException when the creation of the wallet fails for other reasons besides that the wallet already
     * exists.
     */
    public static void tryCreateWallet(WalletConfig config) throws WalletException {
        tryToCreateWallet(config.config(), config.credential());
    }

    private static void tryToCreateWallet(String walletConfig, String walletCredentials) throws WalletException {
        try {
            Wallet.createWallet(walletConfig, walletCredentials).get();
        }
        catch (WalletExistsException ignored) {} // This is ok, we want to only create if wallet don't exist
        catch (IndyException | InterruptedException | ExecutionException e) {
            if( !(e.getCause() != null && e.getCause() instanceof WalletExistsException)) {
                throw new WalletException("Unable to try-create wallet", e);
            }
            // This is ok, we want to only create if wallet don't exist
        }
    }
}
