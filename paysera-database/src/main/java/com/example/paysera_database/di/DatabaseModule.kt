package com.example.paysera_database.di

import android.content.Context
import com.example.paysera_database.db.DatabaseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabaseRepository(
    @ApplicationContext context: Context,
  ): DatabaseRepositoryImpl {
    return DatabaseRepositoryImpl(context)
  }
}