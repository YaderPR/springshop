// lib/src/features/home/presentation/screens/ebay_home_screen.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/categories/presentation/widgets/categories_list_widget.dart';
import '../widgets/appbar_widget.dart';
import '../widgets/auth_prompt_widget.dart';
import '../widgets/bottom_nav_bar_widget.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      backgroundColor: Colors.black,
      
      appBar: PreferredSize(
        preferredSize: Size.fromHeight(120.0),
        child: AppBarWidget(),
      ),

      body: SingleChildScrollView(
        child: Column(
          children: [
            SizedBox(height: 10),
            AuthPromptWidget(),
            CategoriesListWidget(),
          ],
        ),
      ),
      
      bottomNavigationBar: BottomNavBarWidget(),
    );
  }
}