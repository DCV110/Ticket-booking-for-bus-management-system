package com.example.btms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalletActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvBalance;
    private RecyclerView rvTransactions;
    private View cardNoHistory;
    private String userEmail;
    private TransactionAdapter transactionAdapter;
    private List<android.content.ContentValues> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("BTMS_PREFS", MODE_PRIVATE);
        userEmail = sharedPreferences.getString("user_email", null);

        if (userEmail == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvBalance = findViewById(R.id.tvBalance);
        rvTransactions = findViewById(R.id.rvTransactions);
        cardNoHistory = findViewById(R.id.cardNoHistory);
        Button btnDeposit = findViewById(R.id.btnDeposit);
        Button btnWithdraw = findViewById(R.id.btnWithdraw);

        transactions = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(this));
        rvTransactions.setAdapter(transactionAdapter);

        btnDeposit.setOnClickListener(v -> showDepositDialog());
        btnWithdraw.setOnClickListener(v -> showWithdrawDialog());

        loadWalletData();

        // Setup bottom navigation
        View rootView = findViewById(android.R.id.content);
        BottomNavHelper.setupBottomNav(rootView, R.id.navWallet);
        BottomNavHelper.setupBottomNavListeners(this, rootView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWalletData();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        super.onDestroy();
    }

    private void loadWalletData() {
        if (userEmail == null) return;

        // Load balance
        double balance = dbHelper.getWalletBalance(userEmail);
        tvBalance.setText(CurrencyHelper.formatVND(balance));

        // Load transactions
        transactions.clear();
        List<android.content.ContentValues> walletTransactions = dbHelper.getWalletTransactions(userEmail, 50);
        transactions.addAll(walletTransactions);
        transactionAdapter.notifyDataSetChanged();

        // Show/hide no history card
        if (transactions.isEmpty()) {
            cardNoHistory.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
        } else {
            cardNoHistory.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
        }
    }

    private void showDepositDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_wallet_transaction, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        tvTitle.setText("Nạp tiền vào ví");
        etDescription.setHint("Ghi chú (tùy chọn)");

        AlertDialog dialog = builder.create();
        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                String description = etDescription.getText().toString().trim();
                if (description.isEmpty()) {
                    description = "Nạp tiền vào ví";
                }

                if (dbHelper.depositToWallet(userEmail, amount, description)) {
                    Toast.makeText(this, "Nạp tiền thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadWalletData();
                } else {
                    Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void showWithdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_wallet_transaction, null);
        builder.setView(dialogView);

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etAmount = dialogView.findViewById(R.id.etAmount);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        tvTitle.setText("Rút tiền từ ví");
        etDescription.setHint("Ghi chú (tùy chọn)");

        AlertDialog dialog = builder.create();
        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    Toast.makeText(this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    return;
                }

                double currentBalance = dbHelper.getWalletBalance(userEmail);
                if (amount > currentBalance) {
                    Toast.makeText(this, "Số dư không đủ. Số dư hiện tại: " + CurrencyHelper.formatVND(currentBalance), Toast.LENGTH_LONG).show();
                    return;
                }

                String description = etDescription.getText().toString().trim();
                if (description.isEmpty()) {
                    description = "Rút tiền từ ví";
                }

                if (dbHelper.withdrawFromWallet(userEmail, amount, description)) {
                    Toast.makeText(this, "Rút tiền thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadWalletData();
                } else {
                    Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private static class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
        private List<android.content.ContentValues> transactions;

        public TransactionAdapter(List<android.content.ContentValues> transactions) {
            this.transactions = transactions;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            android.content.ContentValues transaction = transactions.get(position);
            
            String type = transaction.getAsString("type");
            double amount = transaction.getAsDouble("amount");
            String description = transaction.getAsString("description");
            String date = transaction.getAsString("transaction_date");

            // Set transaction type text
            String typeText = "";
            int amountColor = 0;
            String amountPrefix = "";
            
            if ("deposit".equals(type)) {
                typeText = "Nạp tiền";
                amountColor = android.graphics.Color.parseColor("#4CAF50"); // Green
                amountPrefix = "+";
            } else if ("withdraw".equals(type)) {
                typeText = "Rút tiền";
                amountColor = android.graphics.Color.parseColor("#F44336"); // Red
                amountPrefix = "-";
            } else if ("payment".equals(type)) {
                typeText = "Thanh toán";
                amountColor = android.graphics.Color.parseColor("#F44336"); // Red
                amountPrefix = "-";
            }

            holder.tvTransactionType.setText(typeText);
            holder.tvTransactionDescription.setText(description != null ? description : "");
            holder.tvTransactionAmount.setText(amountPrefix + CurrencyHelper.formatVND(amount));
            holder.tvTransactionAmount.setTextColor(amountColor);

            // Format date
            if (date != null && !date.isEmpty()) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    Date dateObj = inputFormat.parse(date);
                    if (dateObj != null) {
                        holder.tvTransactionDate.setText(outputFormat.format(dateObj));
                    } else {
                        holder.tvTransactionDate.setText(date);
                    }
                } catch (Exception e) {
                    holder.tvTransactionDate.setText(date);
                }
            } else {
                holder.tvTransactionDate.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTransactionType;
            TextView tvTransactionDescription;
            TextView tvTransactionAmount;
            TextView tvTransactionDate;

            ViewHolder(View itemView) {
                super(itemView);
                tvTransactionType = itemView.findViewById(R.id.tvTransactionType);
                tvTransactionDescription = itemView.findViewById(R.id.tvTransactionDescription);
                tvTransactionAmount = itemView.findViewById(R.id.tvTransactionAmount);
                tvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            }
        }
    }
}
