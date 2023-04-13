package me.shurik.bettersuggestions.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NumberRangeArgumentType;
import net.minecraft.command.argument.RegistryEntryPredicateArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.argument.SwizzleArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.command.BossBarCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.DataCommand.ObjectType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

public class NoopExecuteCommand {
	private static final Dynamic2CommandExceptionType BLOCKS_TOOBIG_EXCEPTION = new Dynamic2CommandExceptionType((maxCount, count) -> {
		return Text.translatable("commands.execute.blocks.toobig", maxCount, count);
	});
	private static final SimpleCommandExceptionType CONDITIONAL_FAIL_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.execute.conditional.fail"));
	private static final DynamicCommandExceptionType CONDITIONAL_FAIL_COUNT_EXCEPTION = new DynamicCommandExceptionType((count) -> {
		return Text.translatable("commands.execute.conditional.fail_count", count);
	});
	private static final BinaryOperator<ResultConsumer<ServerCommandSource>> BINARY_RESULT_CONSUMER = (consumer, consumer2) -> {
		return (context, success, result) -> {
			consumer.onCommandComplete(context, success, result);
			consumer2.onCommandComplete(context, success, result);
		};
	};
	private static final SuggestionProvider<ServerCommandSource> LOOT_CONDITIONS = (context, builder) -> {
		LootConditionManager lootConditionManager = ((ServerCommandSource)context.getSource()).getServer().getPredicateManager();
		return CommandSource.suggestIdentifiers((Iterable<Identifier>)lootConditionManager.getIds(), builder);
	};

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess) {
		LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("execute").requires((source) -> {
			return source.hasPermissionLevel(2);
		}));
		dispatcher.register(((((((((((((CommandManager.literal("execute").requires((source) -> {
			return source.hasPermissionLevel(2);
		})).then(CommandManager.literal("run").redirect(dispatcher.getRoot()))).then(addConditionArguments(literalCommandNode, CommandManager.literal("if"), true, commandRegistryAccess))).then(addConditionArguments(literalCommandNode, CommandManager.literal("unless"), false, commandRegistryAccess))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
			List<ServerCommandSource> list = Lists.newArrayList();
			Iterator<? extends Entity> var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

			while(var2.hasNext()) {
				Entity entity = (Entity)var2.next();
				list.add(((ServerCommandSource)context.getSource()).withEntity(entity));
			}

			return list;
		})))).then(CommandManager.literal("at").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
			List<ServerCommandSource> list = Lists.newArrayList();
			Iterator<? extends Entity> var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

			while(var2.hasNext()) {
				Entity entity = (Entity)var2.next();
				list.add(((ServerCommandSource)context.getSource()).withWorld((ServerWorld)entity.world).withPosition(entity.getPos()).withRotation(entity.getRotationClient()));
			}

			return list;
		})))).then((CommandManager.literal("store").then(addStoreArguments(literalCommandNode, CommandManager.literal("result"), true))).then(addStoreArguments(literalCommandNode, CommandManager.literal("success"), false)))).then((CommandManager.literal("positioned").then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, (context) -> {
			return ((ServerCommandSource)context.getSource()).withPosition(Vec3ArgumentType.getVec3(context, "pos")).withEntityAnchor(EntityAnchorArgumentType.EntityAnchor.FEET);
		}))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
			List<ServerCommandSource> list = Lists.newArrayList();
			Iterator<? extends Entity> var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

			while(var2.hasNext()) {
				Entity entity = (Entity)var2.next();
				list.add(((ServerCommandSource)context.getSource()).withPosition(entity.getPos()));
			}

			return list;
		}))))).then((CommandManager.literal("rotated").then(CommandManager.argument("rot", RotationArgumentType.rotation()).redirect(literalCommandNode, (context) -> {
			return ((ServerCommandSource)context.getSource()).withRotation(RotationArgumentType.getRotation(context, "rot").toAbsoluteRotation((ServerCommandSource)context.getSource()));
		}))).then(CommandManager.literal("as").then(CommandManager.argument("targets", EntityArgumentType.entities()).fork(literalCommandNode, (context) -> {
			List<ServerCommandSource> list = Lists.newArrayList();
			Iterator<? extends Entity> var2 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

			while(var2.hasNext()) {
				Entity entity = (Entity)var2.next();
				list.add(((ServerCommandSource)context.getSource()).withRotation(entity.getRotationClient()));
			}

			return list;
		}))))).then((CommandManager.literal("facing").then(CommandManager.literal("entity").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).fork(literalCommandNode, (context) -> {
			List<ServerCommandSource> list = Lists.newArrayList();
			EntityAnchorArgumentType.EntityAnchor entityAnchor = EntityAnchorArgumentType.getEntityAnchor(context, "anchor");
			Iterator<? extends Entity> var3 = EntityArgumentType.getOptionalEntities(context, "targets").iterator();

			while(var3.hasNext()) {
				Entity entity = (Entity)var3.next();
				list.add(((ServerCommandSource)context.getSource()).withLookingAt(entity, entityAnchor));
			}

			return list;
		}))))).then(CommandManager.argument("pos", Vec3ArgumentType.vec3()).redirect(literalCommandNode, (context) -> {
			return ((ServerCommandSource)context.getSource()).withLookingAt(Vec3ArgumentType.getVec3(context, "pos"));
		})))).then(CommandManager.literal("align").then(CommandManager.argument("axes", SwizzleArgumentType.swizzle()).redirect(literalCommandNode, (context) -> {
			return ((ServerCommandSource)context.getSource()).withPosition(((ServerCommandSource)context.getSource()).getPosition().floorAlongAxes(SwizzleArgumentType.getSwizzle(context, "axes")));
		})))).then(CommandManager.literal("anchored").then(CommandManager.argument("anchor", EntityAnchorArgumentType.entityAnchor()).redirect(literalCommandNode, (context) -> {
			return ((ServerCommandSource)context.getSource()).withEntityAnchor(EntityAnchorArgumentType.getEntityAnchor(context, "anchor"));
		})))).then(CommandManager.literal("in").then(CommandManager.argument("dimension", DimensionArgumentType.dimension()).redirect(literalCommandNode, (context) -> {
			return ((ServerCommandSource)context.getSource()).withWorld(DimensionArgumentType.getDimensionArgument(context, "dimension"));
		}))));
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addStoreArguments(LiteralCommandNode<ServerCommandSource> node, LiteralArgumentBuilder<ServerCommandSource> builder, boolean requestResult) {
		builder.then(CommandManager.literal("score").then(CommandManager.argument("targets", ScoreHolderArgumentType.scoreHolders()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(CommandManager.argument("objective", ScoreboardObjectiveArgumentType.scoreboardObjective()).redirect(node, (context) -> {
			return executeStoreScore((ServerCommandSource)context.getSource(), ScoreHolderArgumentType.getScoreboardScoreHolders(context, "targets"), ScoreboardObjectiveArgumentType.getObjective(context, "objective"), requestResult);
		}))));
		builder.then(CommandManager.literal("bossbar").then((CommandManager.argument("id", IdentifierArgumentType.identifier()).suggests(BossBarCommand.SUGGESTION_PROVIDER).then(CommandManager.literal("value").redirect(node, (context) -> {
			return executeStoreBossbar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), true, requestResult);
		}))).then(CommandManager.literal("max").redirect(node, (context) -> {
			return executeStoreBossbar((ServerCommandSource)context.getSource(), BossBarCommand.getBossBar(context), false, requestResult);
		}))));
		Iterator<ObjectType> var3 = DataCommand.TARGET_OBJECT_TYPES.iterator();

		while(var3.hasNext()) {
			DataCommand.ObjectType objectType = (DataCommand.ObjectType)var3.next();
			objectType.addArgumentsToBuilder(builder, (builderx) -> {
				return builderx.then((((((CommandManager.argument("path", NbtPathArgumentType.nbtPath()).then(CommandManager.literal("int").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
					return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
						return NbtInt.of((int)((double)result * DoubleArgumentType.getDouble(context, "scale")));
					}, requestResult);
				})))).then(CommandManager.literal("float").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
					return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
						return NbtFloat.of((float)((double)result * DoubleArgumentType.getDouble(context, "scale")));
					}, requestResult);
				})))).then(CommandManager.literal("short").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
					return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
						return NbtShort.of((short)((int)((double)result * DoubleArgumentType.getDouble(context, "scale"))));
					}, requestResult);
				})))).then(CommandManager.literal("long").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
					return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
						return NbtLong.of((long)((double)result * DoubleArgumentType.getDouble(context, "scale")));
					}, requestResult);
				})))).then(CommandManager.literal("double").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
					return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
						return NbtDouble.of((double)result * DoubleArgumentType.getDouble(context, "scale"));
					}, requestResult);
				})))).then(CommandManager.literal("byte").then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).redirect(node, (context) -> {
					return executeStoreData((ServerCommandSource)context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), (result) -> {
						return NbtByte.of((byte)((int)((double)result * DoubleArgumentType.getDouble(context, "scale"))));
					}, requestResult);
				}))));
			});
		}

		return builder;
	}

	private static ServerCommandSource executeStoreScore(ServerCommandSource source, Collection<String> targets, ScoreboardObjective objective, boolean requestResult) {
		return source.mergeConsumers((context, success, result) -> {}, BINARY_RESULT_CONSUMER);
	}

	private static ServerCommandSource executeStoreBossbar(ServerCommandSource source, CommandBossBar bossBar, boolean storeInValue, boolean requestResult) {
		return source.mergeConsumers((context, success, result) -> {}, BINARY_RESULT_CONSUMER);
	}

	private static ServerCommandSource executeStoreData(ServerCommandSource source, DataCommandObject object, NbtPathArgumentType.NbtPath path, IntFunction<NbtElement> nbtSetter, boolean requestResult) {
		return source.mergeConsumers((context, success, result) -> {}, BINARY_RESULT_CONSUMER);
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addConditionArguments(CommandNode<ServerCommandSource> root, LiteralArgumentBuilder<ServerCommandSource> argumentBuilder, boolean positive, CommandRegistryAccess commandRegistryAccess) {
		(((((argumentBuilder.then(CommandManager.literal("block").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(addConditionLogic(root, CommandManager.argument("block", BlockPredicateArgumentType.blockPredicate(commandRegistryAccess)), positive, (context) -> {
			return BlockPredicateArgumentType.getBlockPredicate(context, "block").test(new CachedBlockPosition(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "pos"), true));
		}))))).then(CommandManager.literal("biome").then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()).then(addConditionLogic(root, CommandManager.argument("biome", RegistryEntryPredicateArgumentType.registryEntryPredicate(commandRegistryAccess, RegistryKeys.BIOME)), positive, (context) -> {
			return RegistryEntryPredicateArgumentType.getRegistryEntryPredicate(context, "biome", RegistryKeys.BIOME).test(((ServerCommandSource)context.getSource()).getWorld().getBiome(BlockPosArgumentType.getLoadedBlockPos(context, "pos")));
		}))))).then(CommandManager.literal("score").then(CommandManager.argument("target", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then((((((CommandManager.argument("targetObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()).then(CommandManager.literal("=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
			return testScoreCondition(context, Integer::equals);
		}))))).then(CommandManager.literal("<").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
			return testScoreCondition(context, (a, b) -> {
				return a < b;
			});
		}))))).then(CommandManager.literal("<=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
			return testScoreCondition(context, (a, b) -> {
				return a <= b;
			});
		}))))).then(CommandManager.literal(">").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
			return testScoreCondition(context, (a, b) -> {
				return a > b;
			});
		}))))).then(CommandManager.literal(">=").then(CommandManager.argument("source", ScoreHolderArgumentType.scoreHolder()).suggests(ScoreHolderArgumentType.SUGGESTION_PROVIDER).then(addConditionLogic(root, CommandManager.argument("sourceObjective", ScoreboardObjectiveArgumentType.scoreboardObjective()), positive, (context) -> {
			return testScoreCondition(context, (a, b) -> {
				return a >= b;
			});
		}))))).then(CommandManager.literal("matches").then(addConditionLogic(root, CommandManager.argument("range", NumberRangeArgumentType.intRange()), positive, (context) -> {
			return testScoreMatch(context, NumberRangeArgumentType.IntRangeArgumentType.getRangeArgument(context, "range"));
		}))))))).then(CommandManager.literal("blocks").then(CommandManager.argument("start", BlockPosArgumentType.blockPos()).then(CommandManager.argument("end", BlockPosArgumentType.blockPos()).then((CommandManager.argument("destination", BlockPosArgumentType.blockPos()).then(addBlocksConditionLogic(root, CommandManager.literal("all"), positive, false))).then(addBlocksConditionLogic(root, CommandManager.literal("masked"), positive, true))))))).then(CommandManager.literal("entity").then((CommandManager.argument("entities", EntityArgumentType.entities()).fork(root, (context) -> {
			return getSourceOrEmptyForConditionFork(context, positive, !EntityArgumentType.getOptionalEntities(context, "entities").isEmpty());
		})).executes(getExistsConditionExecute(positive, (context) -> {
			return EntityArgumentType.getOptionalEntities(context, "entities").size();
		}))))).then(CommandManager.literal("predicate").then(addConditionLogic(root, CommandManager.argument("predicate", IdentifierArgumentType.identifier()).suggests(LOOT_CONDITIONS), positive, (context) -> {
			return testLootCondition((ServerCommandSource)context.getSource(), IdentifierArgumentType.getPredicateArgument(context, "predicate"));
		})));
		Iterator<ObjectType> var4 = DataCommand.SOURCE_OBJECT_TYPES.iterator();

		while(var4.hasNext()) {
			DataCommand.ObjectType objectType = (DataCommand.ObjectType)var4.next();
			argumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("data"), (builder) -> {
				return builder.then((CommandManager.argument("path", NbtPathArgumentType.nbtPath()).fork(root, (context) -> {
					return getSourceOrEmptyForConditionFork(context, positive, countPathMatches(objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")) > 0);
				})).executes(getExistsConditionExecute(positive, (context) -> {
					return countPathMatches(objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"));
				})));
			}));
		}

		return argumentBuilder;
	}

	private static Command<ServerCommandSource> getExistsConditionExecute(boolean positive, ExistsCondition condition) {
		return positive ? (context) -> {
			int i = condition.test(context);
			if (i > 0) {
				((ServerCommandSource)context.getSource()).sendFeedback(Text.translatable("commands.execute.conditional.pass_count", i), false);
				return i;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		} : (context) -> {
			int i = condition.test(context);
			if (i == 0) {
				((ServerCommandSource)context.getSource()).sendFeedback(Text.translatable("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(i);
			}
		};
	}

	private static int countPathMatches(DataCommandObject object, NbtPathArgumentType.NbtPath path) throws CommandSyntaxException {
		return path.count(object.getNbt());
	}

	private static boolean testScoreCondition(CommandContext<ServerCommandSource> context, BiPredicate<Integer, Integer> condition) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
		ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
		String string2 = ScoreHolderArgumentType.getScoreHolder(context, "source");
		ScoreboardObjective scoreboardObjective2 = ScoreboardObjectiveArgumentType.getObjective(context, "sourceObjective");
		Scoreboard scoreboard = ((ServerCommandSource)context.getSource()).getServer().getScoreboard();
		if (scoreboard.playerHasObjective(string, scoreboardObjective) && scoreboard.playerHasObjective(string2, scoreboardObjective2)) {
			ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(string, scoreboardObjective);
			ScoreboardPlayerScore scoreboardPlayerScore2 = scoreboard.getPlayerScore(string2, scoreboardObjective2);
			return condition.test(scoreboardPlayerScore.getScore(), scoreboardPlayerScore2.getScore());
		} else {
			return false;
		}
	}

	private static boolean testScoreMatch(CommandContext<ServerCommandSource> context, NumberRange.IntRange range) throws CommandSyntaxException {
		String string = ScoreHolderArgumentType.getScoreHolder(context, "target");
		ScoreboardObjective scoreboardObjective = ScoreboardObjectiveArgumentType.getObjective(context, "targetObjective");
		Scoreboard scoreboard = ((ServerCommandSource)context.getSource()).getServer().getScoreboard();
		return !scoreboard.playerHasObjective(string, scoreboardObjective) ? false : range.test(scoreboard.getPlayerScore(string, scoreboardObjective).getScore());
	}

	private static boolean testLootCondition(ServerCommandSource source, LootCondition condition) {
		ServerWorld serverWorld = source.getWorld();
		LootContext.Builder builder = (new LootContext.Builder(serverWorld)).parameter(LootContextParameters.ORIGIN, source.getPosition()).optionalParameter(LootContextParameters.THIS_ENTITY, source.getEntity());
		return condition.test(builder.build(LootContextTypes.COMMAND));
	}

	private static Collection<ServerCommandSource> getSourceOrEmptyForConditionFork(CommandContext<ServerCommandSource> context, boolean positive, boolean value) {
		return (value == positive ? Collections.singleton((ServerCommandSource)context.getSource()) : Collections.emptyList());
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, Condition condition) {
		return builder.fork(root, (context) -> {
			return getSourceOrEmptyForConditionFork(context, positive, condition.test(context));
		}).executes((context) -> {
			if (positive == condition.test(context)) {
				((ServerCommandSource)context.getSource()).sendFeedback(Text.translatable("commands.execute.conditional.pass"), false);
				return 1;
			} else {
				throw CONDITIONAL_FAIL_EXCEPTION.create();
			}
		});
	}

	private static ArgumentBuilder<ServerCommandSource, ?> addBlocksConditionLogic(CommandNode<ServerCommandSource> root, ArgumentBuilder<ServerCommandSource, ?> builder, boolean positive, boolean masked) {
		return builder.fork(root, (context) -> {
			return getSourceOrEmptyForConditionFork(context, positive, testBlocksCondition(context, masked).isPresent());
		}).executes(positive ? (context) -> {
			return executePositiveBlockCondition(context, masked);
		} : (context) -> {
			return executeNegativeBlockCondition(context, masked);
		});
	}

	private static int executePositiveBlockCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
		OptionalInt optionalInt = testBlocksCondition(context, masked);
		if (optionalInt.isPresent()) {
			((ServerCommandSource)context.getSource()).sendFeedback(Text.translatable("commands.execute.conditional.pass_count", optionalInt.getAsInt()), false);
			return optionalInt.getAsInt();
		} else {
			throw CONDITIONAL_FAIL_EXCEPTION.create();
		}
	}

	private static int executeNegativeBlockCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
		OptionalInt optionalInt = testBlocksCondition(context, masked);
		if (optionalInt.isPresent()) {
			throw CONDITIONAL_FAIL_COUNT_EXCEPTION.create(optionalInt.getAsInt());
		} else {
			((ServerCommandSource)context.getSource()).sendFeedback(Text.translatable("commands.execute.conditional.pass"), false);
			return 1;
		}
	}

	private static OptionalInt testBlocksCondition(CommandContext<ServerCommandSource> context, boolean masked) throws CommandSyntaxException {
		return testBlocksCondition(((ServerCommandSource)context.getSource()).getWorld(), BlockPosArgumentType.getLoadedBlockPos(context, "start"), BlockPosArgumentType.getLoadedBlockPos(context, "end"), BlockPosArgumentType.getLoadedBlockPos(context, "destination"), masked);
	}

	private static OptionalInt testBlocksCondition(ServerWorld world, BlockPos start, BlockPos end, BlockPos destination, boolean masked) throws CommandSyntaxException {
		BlockBox blockBox = BlockBox.create(start, end);
		BlockBox blockBox2 = BlockBox.create(destination, destination.add(blockBox.getDimensions()));
		BlockPos blockPos = new BlockPos(blockBox2.getMinX() - blockBox.getMinX(), blockBox2.getMinY() - blockBox.getMinY(), blockBox2.getMinZ() - blockBox.getMinZ());
		int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
		if (i > 32768) {
			throw BLOCKS_TOOBIG_EXCEPTION.create(32768, i);
		} else {
			int j = 0;

			for(int k = blockBox.getMinZ(); k <= blockBox.getMaxZ(); ++k) {
				for(int l = blockBox.getMinY(); l <= blockBox.getMaxY(); ++l) {
					for(int m = blockBox.getMinX(); m <= blockBox.getMaxX(); ++m) {
						BlockPos blockPos2 = new BlockPos(m, l, k);
						BlockPos blockPos3 = blockPos2.add(blockPos);
						BlockState blockState = world.getBlockState(blockPos2);
						if (!masked || !blockState.isOf(Blocks.AIR)) {
							if (blockState != world.getBlockState(blockPos3)) {
								return OptionalInt.empty();
							}

							BlockEntity blockEntity = world.getBlockEntity(blockPos2);
							BlockEntity blockEntity2 = world.getBlockEntity(blockPos3);
							if (blockEntity != null) {
								if (blockEntity2 == null) {
									return OptionalInt.empty();
								}

								if (blockEntity2.getType() != blockEntity.getType()) {
									return OptionalInt.empty();
								}

								NbtCompound nbtCompound = blockEntity.createNbt();
								NbtCompound nbtCompound2 = blockEntity2.createNbt();
								if (!nbtCompound.equals(nbtCompound2)) {
									return OptionalInt.empty();
								}
							}

							++j;
						}
					}
				}
			}

			return OptionalInt.of(j);
		}
	}

	@FunctionalInterface
	interface Condition {
		boolean test(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	}

	@FunctionalInterface
	interface ExistsCondition {
		int test(CommandContext<ServerCommandSource> context) throws CommandSyntaxException;
	}
}
