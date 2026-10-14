package com.manuel.moute.models;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Persistencia de las tarjetas de demostración contratadas sin iniciar sesión.
 *
 * <p>El JSON se guarda en SharedPreferences privadas de esta instalación, no en
 * Firebase. Cada entrada usa el número de tarjeta como clave, por lo que guardar
 * de nuevo ese número actualiza una tarjeta sin eliminar las demás. No representa
 * un título de transporte real ni un pago y no sincroniza entre dispositivos.</p>
 */
public class GuestCardStore {
    private static final String PREFERENCES_NAME = "guest_cards";
    private static final String CARDS_KEY = "cards";
    // Protege el ciclo leer-modificar-escribir entre instancias de este proceso.
    private static final Object STORE_LOCK = new Object();

    private final SharedPreferences preferences;

    /** Prefiere el contexto de aplicación para no retener una pantalla concreta. */
    public GuestCardStore(Context context) {
        Context applicationContext = context.getApplicationContext();
        preferences = (applicationContext == null ? context : applicationContext)
                .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Reconstruye las tarjetas con identificador válido; el orden del JSON no es
     * un orden de presentación. Si el almacenamiento está dañado devuelve una
     * lista vacía sin intentar reemplazar los datos originales.
     */
    public ArrayList<Tarjeta> getCards() {
        synchronized (STORE_LOCK) {
            ArrayList<Tarjeta> result = new ArrayList<>();
            try {
                JSONObject storedCards = readStoredCards();
                Iterator<String> numbers = storedCards.keys();
                while (numbers.hasNext()) {
                    String number = numbers.next();
                    JSONObject storedCard = storedCards.optJSONObject(number);
                    if (storedCard != null) {
                        Tarjeta card = decode(storedCard);
                        if (card.getNumCard() != null && !card.getNumCard().isEmpty()) {
                            result.add(card);
                        }
                    }
                }
            } catch (JSONException | ClassCastException ignored) {
                // Leer datos dañados no debe sobrescribirlos ni cerrar la pantalla.
            }
            return result;
        }
    }

    /**
     * Inserta o actualiza una tarjeta local conservando el resto.
     *
     * @return true si commit confirma la escritura; false si falta identificador,
     *         falla la escritura o el JSON existente no puede recuperarse.
     */
    public boolean save(Tarjeta card) {
        if (card == null || card.getNumCard() == null
                || card.getNumCard().trim().isEmpty()) {
            return false;
        }
        synchronized (STORE_LOCK) {
            try {
                JSONObject encodedCard = encode(card);
                JSONObject storedCards = readStoredCards();
                storedCards.put(card.getNumCard(), encodedCard);
                return preferences.edit().putString(CARDS_KEY, storedCards.toString()).commit();
            } catch (JSONException | ClassCastException ignored) {
                // No se descartan otras tarjetas para reparar silenciosamente el JSON.
                return false;
            }
        }
    }

    /** Permite que la pantalla de detalle distinga una tarjeta local de una de Firebase. */
    public boolean contains(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        synchronized (STORE_LOCK) {
            try {
                return readStoredCards().has(number);
            } catch (JSONException | ClassCastException ignored) {
                return false;
            }
        }
    }

    /**
     * Elimina únicamente la entrada indicada y confirma la escritura local.
     * Una clave inexistente no es un error; no afecta a las tarjetas de la cuenta.
     */
    public boolean remove(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        synchronized (STORE_LOCK) {
            try {
                JSONObject storedCards = readStoredCards();
                storedCards.remove(number);
                return preferences.edit().putString(CARDS_KEY, storedCards.toString()).commit();
            } catch (JSONException | ClassCastException ignored) {
                return false;
            }
        }
    }

    /** Un almacén todavía no creado equivale a un objeto JSON vacío. */
    private JSONObject readStoredCards() throws JSONException {
        return new JSONObject(preferences.getString(CARDS_KEY, "{}"));
    }

    /** El esquema local conserva todos los campos de Tarjeta, incluidos nulos. */
    private static JSONObject encode(Tarjeta card) throws JSONException {
        JSONObject encoded = new JSONObject();
        encoded.put("nombre", jsonString(card.getNombre()));
        encoded.put("apellidos", jsonString(card.getApellidos()));
        encoded.put("dni", jsonString(card.getDni()));
        encoded.put("numCard", jsonString(card.getNumCard()));
        encoded.put("precio", card.getPrecio());
        encoded.put("cantidadViajes", card.getCantidadViajes());
        encoded.put("tipo", jsonString(card.getTipo()));
        encoded.put("titulo", jsonString(card.getTitulo()));
        encoded.put("fechaContrato", jsonString(card.getFechaContrato()));
        encoded.put("estado", card.isEstado());
        return encoded;
    }

    /** Tolera campos ausentes con los valores por defecto del modelo. */
    private static Tarjeta decode(JSONObject stored) {
        Tarjeta card = new Tarjeta();
        card.setNombre(nullableString(stored, "nombre"));
        card.setApellidos(nullableString(stored, "apellidos"));
        card.setDni(nullableString(stored, "dni"));
        card.setNumCard(nullableString(stored, "numCard"));
        card.setPrecio((float) stored.optDouble("precio", 0));
        card.setCantidadViajes(stored.optInt("cantidadViajes", 0));
        card.setTipo(nullableString(stored, "tipo"));
        card.setTitulo(nullableString(stored, "titulo"));
        card.setFechaContrato(nullableString(stored, "fechaContrato"));
        card.setEstado(stored.optBoolean("estado", false));
        return card;
    }

    // JSONObject usa su propio marcador NULL para conservar los valores nulos.
    private static Object jsonString(String value) {
        return value == null ? JSONObject.NULL : value;
    }

    private static String nullableString(JSONObject stored, String key) {
        return stored.isNull(key) ? null : stored.optString(key, null);
    }
}
