package uz.alien.dictup.domain.usecase.home

data class MainUseCases(
    val getAllWordsUseCase: GetAllWordsUseCase,
    val getAllStoryUseCase: GetAllStoryUseCase,
    val getAllNativeWordUseCase: GetAllNativeWordUseCase,
    val getAllNativeStoryUseCase: GetAllNativeStoryUseCase,
    val getScoreOfBeginnerUseCase: GetScoreOfBeginnerUseCase,
    val getScoreOfEssentialUseCase: GetScoreOfEssentialUseCase,
    val isSyncCompletedUseCase: IsSyncCompletedUseCase,
    val getDataStoreRepositoryUseCase: GetDataStoreRepositoryUseCase
)