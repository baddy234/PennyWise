package com.example.data.model

import java.util.Calendar

data class FinancialAdviceQuote(
    val id: Int,
    val quote: String,
    val author: String,
    val category: String, // e.g. "Investing", "Budgeting", "Mindset", "Saving"
    val actionTip: String
)

object FinancialAdviceRepository {
    val quotes = listOf(
        FinancialAdviceQuote(
            id = 1,
            quote = "Do not save what is left after spending, but spend what is left after saving.",
            author = "Warren Buffett",
            category = "Saving First",
            actionTip = "Automate 20% of your income into savings before budgeting for daily expenses."
        ),
        FinancialAdviceQuote(
            id = 2,
            quote = "Beware of little expenses. A small leak will sink a great ship.",
            author = "Benjamin Franklin",
            category = "Budgeting",
            actionTip = "Audit monthly subscriptions & micro-purchases to catch hidden cash drains."
        ),
        FinancialAdviceQuote(
            id = 3,
            quote = "It's not how much money you make, but how much money you keep.",
            author = "Robert Kiyosaki",
            category = "Wealth Mindset",
            actionTip = "Focus on asset growth and debt reduction rather than lifestyle inflation."
        ),
        FinancialAdviceQuote(
            id = 4,
            quote = "A budget is telling your money where to go instead of wondering where it went.",
            author = "Dave Ramsey",
            category = "Financial Discipline",
            actionTip = "Assign every incoming dollar a specific job using strict fund allocations."
        ),
        FinancialAdviceQuote(
            id = 5,
            quote = "The stock market is a device for transferring money from the impatient to the patient.",
            author = "Warren Buffett",
            category = "Investing",
            actionTip = "Maintain an emergency fund so you never have to liquidate investments early."
        ),
        FinancialAdviceQuote(
            id = 6,
            quote = "An investment in knowledge pays the best interest.",
            author = "Benjamin Franklin",
            category = "Self-Growth",
            actionTip = "Spend 15 minutes a day learning about personal finance or index funds."
        ),
        FinancialAdviceQuote(
            id = 7,
            quote = "Financial freedom is available to those who learn about it and work for it.",
            author = "Robert Kiyosaki",
            category = "Freedom",
            actionTip = "Track daily transactions consistently to maintain total balance clarity."
        ),
        FinancialAdviceQuote(
            id = 8,
            quote = "Rule No. 1: Never lose money. Rule No. 2: Never forget rule No. 1.",
            author = "Warren Buffett",
            category = "Risk Management",
            actionTip = "Set category limit warnings so you never accidentally overspend."
        ),
        FinancialAdviceQuote(
            id = 9,
            quote = "Too many people spend money they haven't earned, to buy things they don't want, to impress people they don't like.",
            author = "Will Rogers",
            category = "Living Within Means",
            actionTip = "Keep total monthly living expenses under 80% of net income to avoid credit reliance and lifestyle inflation."
        ),
        FinancialAdviceQuote(
            id = 10,
            quote = "There is no dignity quite so impressive, and no independence quite so important, as living within your means.",
            author = "Calvin Coolidge",
            category = "Financial Discipline",
            actionTip = "Live below what you make today so you can live with full freedom tomorrow without debt stress."
        ),
        FinancialAdviceQuote(
            id = 11,
            quote = "Never depend on a single income. Make investment to create a second source.",
            author = "Warren Buffett",
            category = "Multiple Income Streams",
            actionTip = "Diversify your earnings by building side projects, freelancing, or investing in dividend-yielding assets."
        ),
        FinancialAdviceQuote(
            id = 12,
            quote = "If you don't find a way to make money while you sleep, you will work until you die.",
            author = "Warren Buffett",
            category = "Passive Income",
            actionTip = "Allocate regular funds toward automated index funds, high-yield deposits, or cash-flow assets."
        ),
        FinancialAdviceQuote(
            id = 13,
            quote = "Do not bite at the bait of pleasure, till you know there is no hook beneath it.",
            author = "Thomas Jefferson",
            category = "Living Within Means",
            actionTip = "Practice the 48-hour rule: wait 2 days before completing non-essential impulse purchases."
        ),
        FinancialAdviceQuote(
            id = 14,
            quote = "The average millionaire has seven streams of income.",
            author = "Tom Corley",
            category = "Multiple Income Streams",
            actionTip = "Layer your income: primary salary, interest/dividends, rental/capital gains, and digital side hustles."
        )
    )

    fun getTodayAdvice(): FinancialAdviceQuote {
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val index = (dayOfYear - 1) % quotes.size
        return quotes[index]
    }

    fun getTimeBasedGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            in 17..21 -> "Good evening"
            else -> "Hello Night Owl"
        }
    }
}
