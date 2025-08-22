package net.runelite.client.live.inDevelopment.biggs.BMacro.handler;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.client.callback.ClientThread;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Manages up to 30 quick‐bank loadouts. Each loadout has:
 *   • enabled (boolean)
 *   • name (String)
 *   • gearItems (List<String>, e.g. "Bearhead [1]")
 *   • inventoryItems (List<String>, e.g. "Anglerfish [3]")
 *
 * The GUI allows:
 *   • Toggling “Enabled” per slot (no limit on how many).
 *   • Editing name, gear list, inventory list (plain text areas).
 *   • Copy Gear / Copy Inventory / Copy Gear+Inv into the selected slot,
 *     where each distinct item is shown as “<ItemName> [count]” and the “[count]” is colored orange.
 */
@Singleton
public class BankLoadoutHandler
{
    private static final String DIRECTORY_NAME = "PolarMacro";
    private static final String FILE_NAME      = "quick_bank_presets.json";

    private final Client client;
    private final ClientThread clientThread;
    private final Path presetsJsonPath;

    /** Exactly 30 slots (indices 0–29). Some entries may be null if unused. */
    private final List<BankPreset> presets = new ArrayList<>(Collections.nCopies(30, null));

    @Inject
    public BankLoadoutHandler(final Client client, final ClientThread clientThread)
    {
        this.client = client;
        this.clientThread = clientThread;

        String runeliteDir = System.getProperty("user.home") + File.separator + ".runelite";
        Path pluginFolder = Paths.get(runeliteDir, DIRECTORY_NAME);
        if (!Files.exists(pluginFolder))
        {
            try
            {
                Files.createDirectories(pluginFolder);
            }
            catch (IOException ex)
            {
                System.err.println("Failed to create plugin directory: " + pluginFolder);
                ex.printStackTrace();
            }
        }

        presetsJsonPath = pluginFolder.resolve(FILE_NAME);
        loadFromDisk();
    }

    /** Returns an unmodifiable copy of the 30‐slot list (some entries may be null). */
    public List<BankPreset> getAllPresets()
    {
        return Collections.unmodifiableList(new ArrayList<>(presets));
    }

    /**
     * Opens a Swing dialog allowing the user to edit up to 30 loadouts at once.
     * You can toggle “Enabled” for each slot (no limit), enter a name, gear, and inventory text.
     * “Copy” buttons gather the player’s current gear/inventory, build lines of “ItemName [count]”,
     * and color the “[count]” in orange using HTML in JEditorPane.
     */
    public void openEditDialog()
    {
        SwingUtilities.invokeLater(() ->
        {
            JDialog dialog = new JDialog((Frame) null, "Edit Quick-Bank Loadouts", true);
            dialog.setLayout(new BorderLayout());
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            // ---------- TOP PANEL: slot selector + copy buttons ----------
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
            topPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

            JLabel slotLabel = new JLabel("Loadout:");
            Integer[] slotNumbers = new Integer[30];
            for (int i = 0; i < 30; i++)
            {
                slotNumbers[i] = i + 1;
            }
            JComboBox<Integer> slotCombo = new JComboBox<>(slotNumbers);
            slotCombo.setSelectedIndex(0);

            JButton copyGearBtn = new JButton("Copy Gear");
            JButton copyInvBtn  = new JButton("Copy Inventory");
            JButton copyBothBtn = new JButton("Copy Gear+Inv");

            topPanel.add(slotLabel);
            topPanel.add(slotCombo);
            topPanel.add(copyGearBtn);
            topPanel.add(copyInvBtn);
            topPanel.add(copyBothBtn);

            dialog.add(topPanel, BorderLayout.NORTH);

            // ---------- MAIN PANEL: 30 rows of (Enabled, Name, Gear, Inventory) ----------
            JPanel mainPanel = new JPanel(new GridLayout(30, 1, 0, 4));
            mainPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

            List<JCheckBox> enabledBoxes = new ArrayList<>(30);
            List<JTextField> nameFields   = new ArrayList<>(30);
            List<JTextArea>   gearAreas    = new ArrayList<>(30);
            List<JEditorPane> gearPanes    = new ArrayList<>(30);
            List<JTextArea>   invAreas     = new ArrayList<>(30);
            List<JEditorPane> invPanes     = new ArrayList<>(30);

            for (int i = 0; i < 30; i++)
            {
                JPanel row = new JPanel(new GridLayout(1, 4, 4, 0));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                BankPreset preset = presets.get(i);
                boolean isEnabled  = (preset != null && preset.isEnabled());

                // Enabled checkbox
                JCheckBox enabledCheck = new JCheckBox("Enabled", isEnabled);
                enabledBoxes.add(enabledCheck);

                // Name field
                String nameVal = (preset != null ? preset.getName() : "");
                JTextField nameField = new JTextField(nameVal);
                nameFields.add(nameField);

                // Gear area: plain JTextArea for editing
                String gearText = (preset != null ? String.join("\n", preset.getGearItems()) : "");
                JTextArea gearArea = new JTextArea(gearText, 4, 20);
                gearAreas.add(gearArea);

                // Gear pane: JEditorPane (HTML) for colored “[count]” preview
                JEditorPane gearPane = new JEditorPane("text/html", "");
                gearPane.setEditable(false);
                gearPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                gearPane.setPreferredSize(new Dimension(200, gearPane.getFontMetrics(gearPane.getFont()).getHeight() * 4));
                gearPanes.add(gearPane);
                setHtmlInEditorPane(gearPane, gearText);

                // Inventory area: plain JTextArea for editing
                String invText = (preset != null ? String.join("\n", preset.getInventoryItems()) : "");
                JTextArea invArea = new JTextArea(invText, 4, 20);
                invAreas.add(invArea);

                // Inventory pane: JEditorPane (HTML) for colored “[count]” preview
                JEditorPane invPane = new JEditorPane("text/html", "");
                invPane.setEditable(false);
                invPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
                invPane.setPreferredSize(new Dimension(200, invPane.getFontMetrics(invPane.getFont()).getHeight() * 4));
                invPanes.add(invPane);
                setHtmlInEditorPane(invPane, invText);

                // Layout: Enabled, Name, Gear‐scroll, Inventory‐scroll
                row.add(enabledCheck);
                row.add(nameField);
                row.add(new JScrollPane(gearArea));
                row.add(new JScrollPane(invArea));

                mainPanel.add(row);
            }

            JScrollPane mainScroll = new JScrollPane(mainPanel);
            dialog.add(mainScroll, BorderLayout.CENTER);

            // ---------- BOTTOM PANEL: Save / Cancel ----------
            JButton saveButton   = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            // ---------- COPY BUTTON ACTIONS ----------

            copyGearBtn.addActionListener(e ->
            {
                int slotIndex = slotCombo.getSelectedIndex();
                clientThread.invoke(() ->
                {
                    Map<String, Integer> gearCounts = new LinkedHashMap<>();
                    if (client.getItemContainer(InventoryID.EQUIPMENT) != null)
                    {
                        for (Item item : client.getItemContainer(InventoryID.EQUIPMENT).getItems())
                        {
                            if (item != null && item.getId() != -1)
                            {
                                String nameItem = client.getItemDefinition(item.getId()).getName();
                                gearCounts.merge(nameItem, 1, Integer::sum);
                            }
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : gearCounts.entrySet())
                    {
                        sb.append(entry.getKey()).append(" [").append(entry.getValue()).append("]\n");
                    }
                    String result = sb.toString();

                    SwingUtilities.invokeLater(() ->
                    {
                        gearAreas.get(slotIndex).setText(result);
                        setHtmlInEditorPane(gearPanes.get(slotIndex), result);
                    });
                });
            });

            copyInvBtn.addActionListener(e ->
            {
                int slotIndex = slotCombo.getSelectedIndex();
                clientThread.invoke(() ->
                {
                    Map<String, Integer> invCounts = new LinkedHashMap<>();
                    if (client.getItemContainer(InventoryID.INVENTORY) != null)
                    {
                        for (Item item : client.getItemContainer(InventoryID.INVENTORY).getItems())
                        {
                            if (item != null && item.getId() != -1)
                            {
                                String nameItem = client.getItemDefinition(item.getId()).getName();
                                invCounts.merge(nameItem, 1, Integer::sum);
                            }
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : invCounts.entrySet())
                    {
                        sb.append(entry.getKey()).append(" [").append(entry.getValue()).append("]\n");
                    }
                    String result = sb.toString();

                    SwingUtilities.invokeLater(() ->
                    {
                        invAreas.get(slotIndex).setText(result);
                        setHtmlInEditorPane(invPanes.get(slotIndex), result);
                    });
                });
            });

            copyBothBtn.addActionListener(e ->
            {
                int slotIndex = slotCombo.getSelectedIndex();
                clientThread.invoke(() ->
                {
                    // Gear portion
                    Map<String, Integer> gearCounts = new LinkedHashMap<>();
                    if (client.getItemContainer(InventoryID.EQUIPMENT) != null)
                    {
                        for (Item item : client.getItemContainer(InventoryID.EQUIPMENT).getItems())
                        {
                            if (item != null && item.getId() != -1)
                            {
                                String nameItem = client.getItemDefinition(item.getId()).getName();
                                gearCounts.merge(nameItem, 1, Integer::sum);
                            }
                        }
                    }
                    StringBuilder gearSb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : gearCounts.entrySet())
                    {
                        gearSb.append(entry.getKey()).append(" [").append(entry.getValue()).append("]\n");
                    }
                    String gearResult = gearSb.toString();

                    // Inventory portion
                    Map<String, Integer> invCounts = new LinkedHashMap<>();
                    if (client.getItemContainer(InventoryID.INVENTORY) != null)
                    {
                        for (Item item : client.getItemContainer(InventoryID.INVENTORY).getItems())
                        {
                            if (item != null && item.getId() != -1)
                            {
                                String nameItem = client.getItemDefinition(item.getId()).getName();
                                invCounts.merge(nameItem, 1, Integer::sum);
                            }
                        }
                    }
                    StringBuilder invSb = new StringBuilder();
                    for (Map.Entry<String, Integer> entry : invCounts.entrySet())
                    {
                        invSb.append(entry.getKey()).append(" [").append(entry.getValue()).append("]\n");
                    }
                    String invResult = invSb.toString();

                    SwingUtilities.invokeLater(() ->
                    {
                        gearAreas.get(slotIndex).setText(gearResult);
                        invAreas.get(slotIndex).setText(invResult);
                        setHtmlInEditorPane(gearPanes.get(slotIndex), gearResult);
                        setHtmlInEditorPane(invPanes.get(slotIndex), invResult);
                    });
                });
            });

            // ---------- SAVE / CANCEL ACTIONS ----------

            saveButton.addActionListener(e ->
            {
                // (No need to limit “Enabled” count any more.)
                List<BankPreset> newList = new ArrayList<>(30);
                for (int i = 0; i < 30; i++)
                {
                    boolean isEnabled = enabledBoxes.get(i).isSelected();
                    String nameVal   = nameFields.get(i).getText().trim();

                    List<String> gearLines = Arrays.stream(gearAreas.get(i).getText().split("\\R"))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());

                    List<String> invLines = Arrays.stream(invAreas.get(i).getText().split("\\R"))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());

                    if (!isEnabled && nameVal.isEmpty() && gearLines.isEmpty() && invLines.isEmpty())
                    {
                        newList.add(null);
                    }
                    else
                    {
                        newList.add(new BankPreset(isEnabled, nameVal, gearLines, invLines));
                    }
                }

                presets.clear();
                presets.addAll(newList);
                saveToDisk();
                dialog.dispose();
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.setSize(1000, 800);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
    }

    /**
     * Helper: takes raw multiline text like
     *   "Super restore(4) [3]\nAnglerfish [5]\n"
     * and sets it into an HTML‐mode JEditorPane so that “[3]” and “[5]” appear in orange.
     */
    private static void setHtmlInEditorPane(JEditorPane pane, String plainText)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style=\"font-family:sans-serif; font-size:12px; color:black;\">");
        String[] lines = plainText.split("\\R");
        Pattern bracketPattern = Pattern.compile("\\[(\\d+)\\]");
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            if (line.isEmpty())
            {
                sb.append("<br>");
                continue;
            }
            Matcher m = bracketPattern.matcher(line);
            StringBuffer temp = new StringBuffer();
            while (m.find())
            {
                m.appendReplacement(temp, "<span style=\"color:orange\">[" + m.group(1) + "]</span>");
            }
            m.appendTail(temp);
            sb.append(temp.toString());
            if (i < lines.length - 1)
            {
                sb.append("<br>");
            }
        }
        sb.append("</body></html>");
        pane.setText(sb.toString());
    }

    /** Reads presets (including enabled flags) from JSON on disk (up to 30). */
    private void loadFromDisk()
    {
        if (!Files.exists(presetsJsonPath))
        {
            return;
        }
        try (Reader reader = Files.newBufferedReader(presetsJsonPath))
        {
            Type token = new TypeToken<List<BankPreset>>() {}.getType();
            List<BankPreset> fromFile = new Gson().fromJson(reader, token);
            for (int i = 0; i < Math.min(30, fromFile.size()); i++)
            {
                presets.set(i, fromFile.get(i));
            }
        }
        catch (IOException ex)
        {
            System.err.println("Failed to read quick_bank_presets.json: " + ex);
            ex.printStackTrace();
        }
    }

    /** Writes all 30 slots (with enabled flags) to JSON on disk (pretty‐printed). */
    private void saveToDisk()
    {
        try (Writer writer = Files.newBufferedWriter(presetsJsonPath))
        {
            new GsonBuilder().setPrettyPrinting().create().toJson(presets, writer);
        }
        catch (IOException ex)
        {
            System.err.println("Failed to write quick_bank_presets.json: " + ex);
            ex.printStackTrace();
        }
    }

    /**
     * Immutable holder for a single loadout’s data:
     *   • enabled: boolean
     *   • name: String
     *   • gearItems: List<String> (e.g. "Combat bracelet(4) [1]")
     *   • inventoryItems: List<String> (e.g. "Super restore(4) [3]")
     */
    public static class BankPreset
    {
        private final boolean enabled;
        private final String name;
        private final List<String> gearItems;
        private final List<String> inventoryItems;

        public BankPreset(boolean enabled, String name,
                          List<String> gearItems,
                          List<String> inventoryItems)
        {
            this.enabled        = enabled;
            this.name           = name;
            this.gearItems      = Collections.unmodifiableList(new ArrayList<>(gearItems));
            this.inventoryItems = Collections.unmodifiableList(new ArrayList<>(inventoryItems));
        }

        public boolean isEnabled()              { return enabled; }
        public String  getName()                { return name; }
        public List<String> getGearItems()      { return gearItems; }
        public List<String> getInventoryItems() { return inventoryItems; }
    }
}
