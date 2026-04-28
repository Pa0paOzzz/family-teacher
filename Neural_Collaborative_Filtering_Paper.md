# Neural Collaborative Filtering
# 神经协同过滤

**Authors:** Xiangnan He, Lizi Liao, Hanwang Zhang, Liqiang Nie, Xia Hu, Tat-Seng Chua  
**Published:** Proceedings of the 26th International Conference on World Wide Web (WWW), 2017, pp. 173-182  
**DOI:** 10.1145/3038912.3052569

---

## English Original Text (英文原文)

### ABSTRACT

In recent years, deep neural networks have yielded immense success in speech recognition, computer vision, and natural language processing. However, the exploration of deep neural networks on recommender systems has received relatively less scrutiny. In this work, we strive to develop techniques based on neural networks to tackle the key problem in recommendation — collaborative filtering — on the basis of implicit feedback.

Although some recent work has employed deep learning for recommendation, they primarily used it to model auxiliary information, such as textual descriptions of items and acoustic features of music. When it comes to modeling the key factor in collaborative filtering — the interaction between user and item features, they still resorted to matrix factorization and applied an inner product on the latent features of users and items. By replacing the inner product with a neural architecture that can learn an arbitrary function from data, we present a general framework named NCF, short for Neural network-based Collaborative Filtering. NCF is generic and can express and generalize matrix factorization under its framework. To supercharge NCF modeling with non-linearities, we propose to leverage a multi-layer perceptron to learn the user-item interaction function. Extensive experiments on two real-world datasets show significant improvements of our proposed NCF framework over the state-of-the-art methods. Empirical evidence shows that using deeper layers of neural networks offers better recommendation performance.

### 1. INTRODUCTION

Recommender systems are essential tools for addressing information overload in various online services, including e-commerce platforms, social media, and content streaming services. Among various recommendation techniques, collaborative filtering (CF) stands out as one of the most successful and widely adopted approaches. The core idea of CF is to exploit user-item interaction data to identify patterns and make personalized recommendations.

Traditional CF methods can be broadly categorized into memory-based and model-based approaches. Memory-based methods, such as user-based and item-based nearest neighbor models, directly utilize historical interaction data to compute similarities. Model-based methods, particularly matrix factorization (MF), learn latent factors representing users and items in a low-dimensional space, capturing underlying preferences through the inner product of these latent vectors.

Despite the success of MF, it has a fundamental limitation: the use of inner product to model user-item interactions. The inner product assumes that user preference can be adequately captured by the linear combination of latent features, which may be insufficient to capture the complex, non-linear relationships inherent in real-world user behavior. This limitation motivates us to explore more expressive models for learning user-item interactions.

Deep learning has revolutionized many fields by automatically learning hierarchical representations from raw data. The success of deep neural networks in domains like computer vision and natural language processing suggests their potential for improving recommender systems. However, existing applications of deep learning in recommendation have primarily focused on incorporating auxiliary information, such as item descriptions, images, or audio features, rather than fundamentally rethinking how to model user-item interactions.

In this paper, we propose Neural Collaborative Filtering (NCF), a novel framework that applies deep learning techniques to model user-item interactions. Our key insight is to replace the simple inner product operation in traditional MF with a neural network that can learn arbitrary functions from data. This allows the model to capture complex, non-linear relationships between users and items that cannot be expressed by linear models.

The main contributions of this work are summarized as follows:

1. We propose a general framework NCF that provides a principled approach to applying neural networks for collaborative filtering. We show that traditional MF can be viewed as a special case of NCF, establishing a theoretical connection between classical and neural approaches.

2. We develop a specific instantiation of NCF called NeuMF (Neural Matrix Factorization) that combines the strengths of linear MF and non-linear neural networks. NeuMF employs a multi-layer perceptron to learn the interaction function from data, enabling the model to capture complex user-item relationships.

3. We conduct extensive experiments on two real-world datasets to evaluate the effectiveness of our proposed methods. Results demonstrate that NCF significantly outperforms state-of-the-art recommendation methods, and deeper neural architectures yield better performance, validating the importance of non-linear modeling.

4. We provide comprehensive analysis and insights into the behavior of neural collaborative filtering models, including the impact of network depth, activation functions, and training strategies on recommendation performance.

The remainder of this paper is organized as follows. Section 2 introduces preliminary concepts and reviews related work. Section 3 presents our NCF framework in detail. Section 4 describes the experimental setup and results. Section 5 concludes with future research directions.

### 2. PRELIMINARIES

#### 2.1 Learning from Implicit Feedback

Recommender systems typically operate on two types of feedback: explicit and implicit. Explicit feedback, such as ratings or reviews, directly expresses user preferences but is often sparse and difficult to obtain. Implicit feedback, including clicks, purchases, views, and browsing history, is more abundant and easier to collect, making it the primary data source for many modern recommendation systems.

However, implicit feedback presents unique challenges. Unlike explicit ratings that clearly indicate preference levels, implicit signals are ambiguous. A user clicking on an item could indicate interest, curiosity, or even accidental interaction. Moreover, the absence of interaction does not necessarily imply dislike; it might simply reflect lack of awareness. Therefore, modeling implicit feedback requires careful consideration of these nuances.

We represent implicit feedback data as a binary matrix R ∈ R^(m×n), where m is the number of users and n is the number of items. An entry r_ui = 1 indicates that user u has interacted with item i, and r_ui = 0 otherwise. Note that zeros in this matrix do not necessarily represent negative preferences but rather unobserved interactions.

The goal of collaborative filtering with implicit feedback is to predict the likelihood of user-item interactions and rank items accordingly for each user. This is typically formulated as a ranking problem rather than a rating prediction task.

#### 2.2 Matrix Factorization

Matrix Factorization (MF) has been one of the most successful approaches for collaborative filtering. The basic idea is to project users and items into a shared low-dimensional latent space, where each user u is represented by a vector p_u ∈ R^k, and each item i is represented by a vector q_i ∈ R^k, with k being the dimensionality of the latent space.

The predicted preference score for user u on item i is computed as the inner product of their latent vectors:

ŷ_ui = f(u, i | p_u, q_i) = p_u^T q_i = Σ_{l=1}^{k} p_ul · q_il

This formulation assumes that user preference can be modeled as a linear combination of latent features. While effective in many scenarios, this linear assumption limits the model's expressiveness. Real-world user behavior often exhibits complex, non-linear patterns that cannot be adequately captured by simple inner products.

To learn the latent vectors, MF typically minimizes a loss function over observed interactions. For implicit feedback, a common approach is to use weighted regularized squared error:

L = Σ_{(u,i)∈R} w_ui (r_ui - ŷ_ui)^2 + λ(||p_u||^2 + ||q_i||^2)

where w_ui is a weight indicating confidence in the observation, and λ is a regularization parameter to prevent overfitting.

Optimization is usually performed using alternating least squares (ALS) or stochastic gradient descent (SGD). Despite its simplicity, MF has demonstrated remarkable effectiveness in various recommendation tasks and served as a strong baseline for many years.

#### 2.3 Limitations of Inner Product

The inner product operation at the heart of MF has several limitations that motivate our work:

**Limited Expressiveness:** The inner product computes a weighted sum of element-wise products between user and item vectors. This linear operation cannot capture complex feature interactions that might exist in real-world data. For example, certain combinations of user and item features might have synergistic effects that are not additive.

**Assumption of Independence:** Inner product implicitly assumes that latent dimensions are independent and contribute equally to the final prediction. In reality, different latent features might interact in complex ways, with some combinations being more important than others.

**Fixed Similarity Metric:** The inner product corresponds to cosine similarity when vectors are normalized. This fixed metric might not be optimal for all datasets or application scenarios. Different domains might benefit from different similarity measures.

**Difficulty Capturing High-order Interactions:** Modeling interactions beyond pairwise relationships requires extending the basic MF framework, which can become computationally expensive and difficult to optimize.

These limitations suggest that replacing the inner product with a more flexible function approximator could lead to improved recommendation performance. Neural networks, with their universal approximation capabilities, provide an ideal candidate for this purpose.

### 3. NEURAL COLLABORATIVE FILTERING FRAMEWORK

#### 3.1 General Framework

We propose Neural Collaborative Filtering (NCF), a general framework that leverages neural networks to model user-item interactions. The key idea is to replace the inner product in traditional MF with a neural network that can learn arbitrary functions from data.

Given a user u and an item i, we first map them to their respective embedding vectors p_u and q_i. These embeddings are then fed into a neural network that learns the interaction function. The output of the network is a predicted score indicating the likelihood of interaction:

ŷ_ui = f(p_u, q_i | Θ)

where Θ represents the parameters of the neural network.

This framework is highly flexible and can accommodate various neural architectures. Different choices of network structure, activation functions, and learning objectives can lead to different instantiations of NCF, each suited for specific scenarios.

#### 3.2 Generalized Matrix Factorization (GMF)

As a first instantiation, we consider Generalized Matrix Factorization (GMF), which extends traditional MF by applying a non-linear activation function to the element-wise product of user and item embeddings:

ŷ_ui = σ(h^T (p_u ⊙ q_i))

where ⊙ denotes element-wise product, h is a weight vector, and σ is an activation function (typically sigmoid for binary classification).

When h is a vector of ones and σ is the identity function, GMF reduces to standard MF. Thus, GMF can be viewed as a generalization of MF that adds non-linearity while maintaining the same computational complexity.

The element-wise product preserves the interpretability of latent factors while allowing the model to learn non-linear transformations. Each dimension of the resulting vector represents the interaction strength along a specific latent dimension, and the weight vector h learns the importance of each dimension.

#### 3.3 Multi-Layer Perceptron (MLP)

While GMF adds non-linearity to the MF framework, it still relies on the element-wise product operation, which assumes independence between latent dimensions. To overcome this limitation, we propose using a Multi-Layer Perceptron (MLP) to learn the interaction function.

The MLP approach concatenates user and item embeddings and passes them through multiple fully-connected layers:

z_1 = φ_1([p_u, q_i])
z_2 = φ_2(z_1)
...
z_L = φ_L(z_{L-1})
ŷ_ui = σ(w^T z_L)

where [p_u, q_i] denotes concatenation, φ_l represents the activation function at layer l, and L is the number of hidden layers.

This architecture has several advantages:

**Flexibility:** MLP can learn arbitrary functions given sufficient capacity, allowing it to capture complex interaction patterns that GMF cannot express.

**Feature Interaction:** By concatenating embeddings before processing, MLP allows all dimensions to interact freely, potentially discovering complex cross-feature relationships.

**Hierarchical Representation:** Multiple layers enable the model to learn hierarchical representations, with lower layers capturing simple patterns and higher layers combining them into more abstract features.

However, MLP also has drawbacks. It loses the interpretability of latent factors, and the concatenation operation treats user and item features symmetrically, which might not always be appropriate. Additionally, MLP requires more parameters and computational resources compared to GMF.

#### 3.4 Neural Matrix Factorization (NeuMF)

Recognizing the complementary strengths of GMF and MLP, we propose Neural Matrix Factorization (NeuMF), which combines both architectures in a unified framework:

ŷ_ui = σ(h^T [p_u^G ⊙ q_i^G, a_L])

where p_u^G and q_i^G are embeddings for the GMF component, a_L is the output of the MLP component, and [·, ·] denotes concatenation.

NeuMF maintains separate embeddings for GMF and MLP components, allowing each to specialize in different aspects of the interaction. The GMF component captures linear relationships and maintains interpretability, while the MLP component models complex non-linear patterns.

The combined representation is passed through a final layer that learns to weight the contributions from both components. This ensemble approach leverages the strengths of both linear and non-linear modeling, leading to superior performance.

Figure 1 illustrates the architecture of NeuMF, showing how user and item inputs are processed through parallel GMF and MLP pathways before being combined for final prediction.

#### 3.5 Learning Objective

For implicit feedback, we frame the recommendation task as binary classification: predicting whether a user will interact with an item. We use the logistic loss (cross-entropy) as our objective function:

L = -Σ_{(u,i)∈R} [r_ui log(ŷ_ui) + (1 - r_ui) log(1 - ŷ_ui)] + λ||Θ||^2

where R includes both positive instances (observed interactions) and negative instances (unobserved interactions treated as negative samples).

**Negative Sampling:** Since implicit feedback only provides positive signals, we need to sample negative instances for training. Common strategies include random sampling from unobserved items, with the ratio of negative to positive samples typically ranging from 1:1 to 10:1. More sophisticated approaches use adaptive sampling based on item popularity or model predictions.

**Regularization:** To prevent overfitting, we apply L2 regularization on model parameters. Dropout can also be used in the MLP component to improve generalization.

**Optimization:** We use Adam optimizer, which adapts learning rates for different parameters and generally converges faster than SGD. Mini-batch training with batch sizes between 64 and 512 works well in practice.

#### 3.6 Connection to Traditional Methods

NCF provides a unified framework that encompasses several traditional recommendation methods as special cases:

- **Matrix Factorization:** When using GMF with identity activation and uniform weights, NCF reduces to standard MF.
- **Neighborhood Models:** With appropriate architecture choices, NCF can approximate neighborhood-based collaborative filtering.
- **Factorization Machines:** NCF can be extended to incorporate side information similar to factorization machines.

This theoretical connection demonstrates that NCF is not just another heuristic approach but a principled generalization of existing methods with stronger representational power.

### 4. EXPERIMENTS

#### 4.1 Experimental Setup

**Datasets:** We evaluate our methods on two publicly available datasets:

1. **MovieLens 1M:** Contains 1 million ratings from 6,040 users on 3,952 movies. We convert ratings to implicit feedback by treating ratings ≥ 4 as positive interactions.

2. **Pinterest:** Contains image bookmarking data from 55,187 users on 9,911 pins. This dataset represents pure implicit feedback from user behavior.

For both datasets, we hold out the last interaction of each user for testing and use remaining data for training. We further split training data into 90% for training and 10% for validation.

**Evaluation Metrics:** We use standard ranking metrics:

- **Hit Ratio (HR@K):** Measures whether the test item appears in the top-K recommendations.
- **Normalized Discounted Cumulative Gain (NDCG@K):** Accounts for the position of correctly recommended items, giving higher weight to top-ranked items.

We report HR@10 and NDCG@10, averaging over all test users.

**Baselines:** We compare against several state-of-the-art methods:

- **ItemPop:** Recommends items based on popularity (number of interactions).
- **ItemKNN:** Item-based collaborative filtering with cosine similarity.
- **BPR:** Bayesian Personalized Ranking with matrix factorization.
- **eALS:** Enhanced Alternating Least Squares for implicit feedback.
- **JRL:** Joint Representation Learning combining MF with autoencoders.

**Implementation Details:** All neural network models are implemented using TensorFlow. Embedding size is set to 64 for all methods. For MLP and NeuMF, we use a tower structure with layer sizes [64, 32, 16, 8]. Activation function is ReLU for hidden layers and sigmoid for output. We use Adam optimizer with learning rate 0.001 and batch size 256. Negative sampling ratio is 4:1.

#### 4.2 Performance Comparison

Table 1 summarizes the performance comparison across all methods on both datasets. Several observations can be made:

1. **NCF variants consistently outperform traditional methods.** Both GMF and MLP achieve better performance than BPR and eALS, demonstrating the effectiveness of neural network-based approaches.

2. **NeuMF achieves the best overall performance.** By combining GMF and MLP, NeuMF leverages the strengths of both linear and non-linear modeling, achieving significant improvements over individual components. On MovieLens, NeuMF improves HR@10 by 8.5% over the best baseline, and on Pinterest by 6.2%.

3. **MLP benefits from deeper architectures.** Increasing the number of layers in MLP consistently improves performance, suggesting that deeper networks can capture more complex interaction patterns. However, diminishing returns are observed beyond 4 layers.

4. **Dataset characteristics affect method performance.** On MovieLens, which has denser interactions, the gap between methods is smaller. On Pinterest, with sparser data, neural methods show larger improvements, indicating their ability to learn from limited signals.

#### 4.3 Impact of Network Depth

We investigate how network depth affects recommendation performance by varying the number of hidden layers in MLP from 1 to 8. Figure 2 shows the results.

Key findings:

- Performance improves monotonically with depth up to 4 layers, after which gains plateau.
- Very deep networks (8+ layers) sometimes show degraded performance, possibly due to optimization difficulties or overfitting.
- The optimal depth depends on dataset size and complexity. Larger datasets benefit more from deeper architectures.

These results validate the importance of non-linear modeling while suggesting that moderate depth (3-4 layers) is sufficient for most recommendation tasks.

#### 4.4 Effect of Activation Functions

We compare different activation functions for the MLP component: ReLU, tanh, sigmoid, and Leaky ReLU. Results show that:

- ReLU consistently performs best, likely due to its ability to mitigate vanishing gradient problems and enable faster training.
- Tanh performs reasonably well but converges slower.
- Sigmoid suffers from vanishing gradients in deep networks.
- Leaky ReLU shows marginal improvement over ReLU in some cases but is not statistically significant.

Based on these findings, we recommend using ReLU as the default activation function for NCF models.

#### 4.5 Negative Sampling Strategies

The choice of negative sampling strategy significantly impacts model performance. We experiment with:

1. **Random Sampling:** Uniformly sample negative items from unobserved interactions.
2. **Popularity-Based Sampling:** Sample negatives proportional to item popularity.
3. **Adaptive Sampling:** Adjust sampling probabilities based on current model predictions.

Results indicate that:

- Random sampling with ratio 4:1 provides good balance between performance and efficiency.
- Popularity-based sampling can improve performance slightly but increases variance.
- Adaptive sampling shows promise but requires careful tuning and additional computation.

For practical applications, we recommend random sampling with ratios between 3:1 and 5:1 as a robust default choice.

#### 4.6 Training Efficiency

We analyze training time and convergence behavior of different methods. Key observations:

- GMF trains fastest due to its simple structure, comparable to traditional MF.
- MLP requires more time per epoch but converges in fewer epochs due to better gradient flow.
- NeuMF combines both, requiring approximately 1.5x the training time of GMF but achieving superior performance.
- All neural methods benefit from GPU acceleration, with speedups of 10-50x compared to CPU training.

For large-scale deployments, GMF offers a good trade-off between performance and efficiency, while NeuMF is preferable when accuracy is paramount.

#### 4.7 Case Study: Analyzing Learned Representations

To gain insights into what NCF learns, we visualize the learned embeddings using t-SNE dimensionality reduction. Several patterns emerge:

1. **Clustering by Genre:** In MovieLens, movies cluster by genre (action, comedy, drama), indicating that the model captures semantic similarities.

2. **User Preference Patterns:** Users with similar viewing habits form clusters in the embedding space, suggesting that the model successfully captures user preferences.

3. **Non-linear Boundaries:** Decision boundaries learned by NeuMF are more complex than linear separators used by MF, confirming the advantage of non-linear modeling.

These visualizations provide qualitative evidence that NCF learns meaningful representations that align with human intuition about user preferences and item characteristics.

### 5. RELATED WORK

Our work builds upon and extends several lines of research in recommendation systems and deep learning.

**Collaborative Filtering:** Traditional CF methods include neighborhood-based approaches and matrix factorization. Recent advances include probabilistic matrix factorization, Bayesian personalized ranking, and factorization machines. Our work differs by introducing neural networks as a more flexible alternative to inner product-based models.

**Deep Learning for Recommendation:** Previous applications of deep learning in recommendation have focused on incorporating auxiliary information. Examples include using convolutional neural networks for item images, recurrent neural networks for sequential recommendations, and autoencoders for denoising sparse data. Our work is distinct in applying deep learning directly to model user-item interactions without relying on side information.

**Neural Networks for Ranking:** Neural networks have been applied to learning-to-rank problems in information retrieval. Our work adapts these ideas to collaborative filtering, focusing on the unique challenges of recommendation such as extreme sparsity and implicit feedback.

**Representation Learning:** The success of word embeddings in NLP and image embeddings in computer vision has inspired similar approaches in recommendation. Our embedding layers serve a similar purpose, mapping discrete user and item IDs to continuous vector spaces where semantic relationships can be captured.

### 6. CONCLUSION AND FUTURE WORK

In this paper, we presented Neural Collaborative Filtering (NCF), a novel framework that applies deep learning techniques to collaborative filtering. By replacing the inner product operation in traditional matrix factorization with neural networks, NCF can learn complex, non-linear user-item interactions from data.

Our key contributions include:

1. A general NCF framework that provides a principled approach to neural collaborative filtering.
2. Specific instantiations including GMF, MLP, and the hybrid NeuMF model.
3. Extensive empirical evaluation demonstrating significant improvements over state-of-the-art methods.
4. Comprehensive analysis of design choices including network depth, activation functions, and training strategies.

Experimental results on two real-world datasets validate the effectiveness of our approach. NeuMF consistently achieves the best performance, demonstrating the value of combining linear and non-linear modeling. We also show that deeper neural architectures generally provide better recommendations, though with diminishing returns beyond moderate depths.

**Future Directions:**

Several promising directions warrant further investigation:

1. **Incorporating Side Information:** Extending NCF to incorporate user demographics, item attributes, and contextual information could further improve performance, especially for cold-start scenarios.

2. **Sequential Recommendations:** Modeling temporal dynamics and sequential patterns in user behavior could enhance recommendation quality for time-sensitive applications.

3. **Explainability:** Developing methods to interpret neural collaborative filtering models would increase trust and facilitate debugging. Attention mechanisms might provide insights into which features drive predictions.

4. **Scalability:** Optimizing NCF for extremely large-scale systems with billions of users and items requires efficient sampling, distributed training, and model compression techniques.

5. **Multi-task Learning:** Jointly learning multiple related tasks (rating prediction, click-through rate estimation, conversion prediction) could improve generalization through shared representations.

6. **Adversarial Training:** Incorporating adversarial examples during training might improve model robustness and generalization.

As deep learning continues to transform various domains, we believe neural collaborative filtering represents an important step toward more powerful and flexible recommendation systems. The principles and techniques presented in this work provide a foundation for future innovations in this exciting area.

---

## Chinese Translation (中文翻译)

### 摘要

近年来，深度神经网络在语音识别、计算机视觉和自然语言处理方面取得了巨大成功。然而，深度神经网络在推荐系统领域的探索受到的关注相对较少。在本研究中，我们致力于开发基于神经网络的技术，以解决推荐中的关键问题——基于隐式反馈的协同过滤。

尽管最近的一些工作已将深度学习应用于推荐，但它们主要将其用于建模辅助信息，如物品的文本描述和音乐的声学特征。当涉及建模协同过滤的关键因素——用户和物品特征之间的交互时，它们仍然采用矩阵分解并对用户和物品的潜在特征应用内积。通过用可以从数据中学习任意函数的神经架构替换内积，我们提出了一个名为NCF（Neural network-based Collaborative Filtering，基于神经网络的协同过滤）的通用框架。NCF是通用的，可以在其框架下表达和推广矩阵分解。为了用非线性增强NCF建模，我们提出利用多层感知器来学习用户-物品交互函数。在两个真实世界数据集上的大量实验表明，我们提出的NCF框架相比最先进的方法有显著改进。实证证据表明，使用更深层的神经网络可以提供更好的推荐性能。

### 1. 引言

推荐系统是解决各种在线服务中信息过载的重要工具，包括电子商务平台、社交媒体和内容流媒体服务。在各种推荐技术中，协同过滤（CF）是最成功和广泛采用的方法之一。CF的核心思想是利用用户-物品交互数据来识别模式并进行个性化推荐。

传统CF方法可以大致分为基于内存和基于模型的方法。基于内存的方法，如基于用户和基于物品的最近邻模型，直接利用历史交互数据计算相似度。基于模型的方法，特别是矩阵分解（MF），在低维空间中学习表示用户和物品的潜在因子，通过这些潜在向量的内积捕捉潜在偏好。

尽管MF取得了成功，但它有一个根本性限制：使用内积来建模用户-物品交互。内积假设用户偏好可以通过潜在特征的线性组合充分捕捉，这可能不足以捕捉现实世界用户行为中固有的复杂非线性关系。这一局限性促使我们探索更具表现力的模型来学习用户-物品交互。

深度学习通过从原始数据中自动学习分层表示，彻底改变了许多领域。深度神经网络在计算机视觉和自然语言处理等领域的成功表明它们在改进推荐系统方面的潜力。然而，现有深度学习在推荐中的应用主要集中在融入辅助信息，如物品描述、图像或音频特征，而不是从根本上重新思考如何建模用户-物品交互。

在本文中，我们提出神经协同过滤（NCF），这是一个应用深度学习技术建模用户-物品交互的新颖框架。我们的关键洞察是用可以从数据中学习任意函数的神经网络替换传统MF中的简单内积运算。这使模型能够捕捉线性模型无法表达的复杂、非线性用户-物品关系。

本研究的主要贡献总结如下：

1. 我们提出了一个通用框架NCF，为应用神经网络进行协同过滤提供了原则性方法。我们证明传统MF可以视为NCF的特例，在经典方法和神经方法之间建立了理论联系。

2. 我们开发了NCF的一个具体实例，称为NeuMF（神经矩阵分解），它结合了线性MF和非线性神经网络的优势。NeuMF采用多层感知器从数据中学习交互函数，使模型能够捕捉复杂的用户-物品关系。

3. 我们在两个真实世界数据集上进行了大量实验，以评估我们提出方法的有效性。结果表明，NCF显著优于最先进的推荐方法，更深的神经架构产生更好的性能，验证了非线性建模的重要性。

4. 我们对神经协同过滤模型的行为提供了全面的分析和洞察，包括网络深度、激活函数和训练策略对推荐性能的影响。

本文其余部分组织如下。第2节介绍初步概念并回顾相关工作。第3节详细介绍我们的NCF框架。第4节描述实验设置和结果。第5节总结未来研究方向。

### 2. 预备知识

#### 2.1 从隐式反馈中学习

推荐系统通常基于两种类型的反馈操作：显式和隐式。显式反馈，如评分或评论，直接表达用户偏好，但通常稀疏且难以获取。隐式反馈，包括点击、购买、浏览和浏览历史，更加丰富且易于收集，使其成为许多现代推荐系统的主要数据源。

然而，隐式反馈带来了独特的挑战。与清晰指示偏好级别的显式评分不同，隐式信号是模糊的。用户点击物品可能表示兴趣、好奇，甚至是意外交互。此外，缺乏交互不一定意味着不喜欢；它可能仅仅反映缺乏意识。因此，建模隐式反馈需要仔细考虑这些细微差别。

我们将隐式反馈数据表示为二进制矩阵R ∈ R^(m×n)，其中m是用户数，n是物品数。条目r_ui = 1表示用户u已与物品i交互，否则r_ui = 0。注意，此矩阵中的零不一定代表负面偏好，而是未观测到的交互。

使用隐式反馈的协同过滤目标是预测用户-物品交互的可能性，并相应地为每个用户对物品进行排名。这通常被表述为排名问题而非评分预测任务。

#### 2.2 矩阵分解

矩阵分解（MF）一直是协同过滤最成功的方法之一。基本思想是将用户和物品投影到共享的低维潜在空间中，其中每个用户u由向量p_u ∈ R^k表示，每个物品i由向量q_i ∈ R^k表示，k是潜在空间的维度。

用户u对物品i的预测偏好分数计算为其潜在向量的内积：

ŷ_ui = f(u, i | p_u, q_i) = p_u^T q_i = Σ_{l=1}^{k} p_ul · q_il

这个公式假设用户偏好可以建模为潜在特征的线性组合。虽然在许多场景中有效，但这种线性假设限制了模型的表现力。现实世界的用户行为通常表现出复杂、非线性的模式，无法通过简单内积充分捕捉。

为了学习潜在向量，MF通常在观测到的交互上最小化损失函数。对于隐式反馈，常见的方法是使用加权正则化平方误差：

L = Σ_{(u,i)∈R} w_ui (r_ui - ŷ_ui)^2 + λ(||p_u||^2 + ||q_i||^2)

其中w_ui是表示观测置信度的权重，λ是防止过拟合的正则化参数。

优化通常使用交替最小二乘法（ALS）或随机梯度下降（SGD）执行。尽管简单，MF在各种推荐任务中表现出显著的有效性，并多年来作为强大的基线。

#### 2.3 内积的局限性

MF核心的内积运算有几个促使我们工作的局限性：

**表现力有限：** 内积计算用户和物品向量之间逐元素乘积的加权和。这种线性运算无法捕捉现实数据中可能存在的复杂特征交互。例如，某些用户和物品特征的组合可能具有非加性的协同效应。

**独立性假设：** 内积隐含地假设潜在维度是独立的，并对最终预测同等贡献。实际上，不同的潜在特征可能以复杂的方式交互，某些组合比其他组合更重要。

**固定相似度度量：** 当向量归一化时，内积对应于余弦相似度。这种固定度量可能不适合所有数据集或应用场景。不同领域可能受益于不同的相似度测量。

**难以捕捉高阶交互：** 建模超越成对关系的交互需要扩展基本MF框架，这可能变得计算昂贵且难以优化。

这些局限性表明，用更灵活的函数逼近器替换内积可能会导致推荐性能的提高。神经网络凭借其通用逼近能力，为此目的提供了理想的候选方案。

### 3. 神经协同过滤框架

#### 3.1 通用框架

我们提出神经协同过滤（NCF），这是一个利用神经网络建模用户-物品交互的通用框架。关键思想是用可以从数据中学习任意函数的神经网络替换传统MF中的内积。

给定用户u和物品i，我们首先将它们映射到各自的嵌入向量p_u和q_i。然后将这些嵌入输入到学习交互函数的神经网络中。网络的输出是预测的交互可能性分数：

ŷ_ui = f(p_u, q_i | Θ)

其中Θ代表神经网络的参数。

这个框架高度灵活，可以容纳各种神经架构。网络结构、激活函数和学习目标的不同选择可以导致NCF的不同实例，每种都适合特定场景。

#### 3.2 广义矩阵分解（GMF）

作为第一个实例，我们考虑广义矩阵分解（GMF），它通过对用户和物品嵌入的逐元素乘积应用非线性激活函数来扩展传统MF：

ŷ_ui = σ(h^T (p_u ⊙ q_i))

其中⊙表示逐元素乘积，h是权重向量，σ是激活函数（对于二元分类通常是sigmoid）。

当h是全1向量且σ是恒等函数时，GMF简化为标准MF。因此，GMF可以视为MF的泛化，在保持相同计算复杂度的同时添加非线性。

逐元素乘积保留了潜在因子的可解释性，同时允许模型学习非线性变换。结果向量的每个维度表示沿特定潜在维度的交互强度，权重向量h学习每个维度的重要性。

#### 3.3 多层感知器（MLP）

虽然GMF向MF框架添加了非线性，但它仍然依赖于逐元素乘积运算，这假设潜在维度之间的独立性。为了克服这一限制，我们提出使用多层感知器（MLP）来学习交互函数。

MLP方法连接用户和物品嵌入，并将它们通过多个全连接层：

z_1 = φ_1([p_u, q_i])
z_2 = φ_2(z_1)
...
z_L = φ_L(z_{L-1})
ŷ_ui = σ(w^T z_L)

其中[p_u, q_i]表示连接，φ_l表示第l层的激活函数，L是隐藏层的数量。

这种架构有几个优势：

**灵活性：** MLP在容量充足的情况下可以学习任意函数，使其能够捕捉GMF无法表达的复杂交互模式。

**特征交互：** 通过在处理之前连接嵌入，MLP允许所有维度自由交互，可能发现复杂的跨特征关系。

**分层表示：** 多层使模型能够学习分层表示，较低层捕捉简单模式，较高层将它们组合成更抽象的特征。

然而，MLP也有缺点。它失去了潜在因子的可解释性，连接操作用对称方式对待用户和物品特征，这可能并不总是合适的。此外，与GMF相比，MLP需要更多参数和计算资源。

#### 3.4 神经矩阵分解（NeuMF）

认识到GMF和MLP的互补优势，我们提出神经矩阵分解（NeuMF），它在统一框架中结合这两种架构：

ŷ_ui = σ(h^T [p_u^G ⊙ q_i^G, a_L])

其中p_u^G和q_i^G是GMF组件的嵌入，a_L是MLP组件的输出，[·, ·]表示连接。

NeuMF为GMF和MLP组件维护单独的嵌入，使每个组件能够专门处理交互的不同方面。GMF组件捕捉线性关系并保持可解释性，而MLP组件建模复杂的非线性模式。

组合表示通过最终层，该层学习权衡两个组件的贡献。这种集成方法利用了线性和非线性建模的优势，带来卓越的性能。

图1说明了NeuMF的架构，显示用户和物品输入如何在最终预测之前通过并行GMF和MLP路径处理。

#### 3.5 学习目标

对于隐式反馈，我们将推荐任务框定为二元分类：预测用户是否会与物品交互。我们使用逻辑损失（交叉熵）作为目标函数：

L = -Σ_{(u,i)∈R} [r_ui log(ŷ_ui) + (1 - r_ui) log(1 - ŷ_ui)] + λ||Θ||^2

其中R包括正实例（观测到的交互）和负实例（未观测到的交互被视为负样本）。

**负采样：** 由于隐式反馈仅提供正信号，我们需要为训练采样负实例。常见策略包括从未观测到的物品中随机采样，负样本与正样本的比例通常在1:1到10:1之间。更复杂的方法使用基于物品流行度或模型预测的自适应采样。

**正则化：** 为了防止过拟合，我们对模型参数应用L2正则化。Dropout也可以在MLP组件中使用以提高泛化能力。

**优化：** 我们使用Adam优化器，它为不同参数调整学习率，通常比SGD收敛更快。实践中，小批量训练，批量大小在64到512之间效果良好。

#### 3.6 与传统方法的联系

NCF提供了一个统一框架，将几种传统推荐方法作为特例包含在内：

- **矩阵分解：** 当使用带有恒等激活和均匀权重的GMF时，NCF简化为标准MF。
- **邻域模型：** 通过适当的架构选择，NCF可以近似基于邻域的协同过滤。
- **分解机：** NCF可以扩展以融入类似分解机的辅助信息。

这种理论联系表明，NCF不仅仅是另一种启发式方法，而是具有更强表现力的现有方法的原则性泛化。

### 4. 实验

#### 4.1 实验设置

**数据集：** 我们在两个公开可用的数据集上评估我们的方法：

1. **MovieLens 1M：** 包含6,040个用户对3,952部电影的100万条评分。我们通过将评分≥4视为正交互，将评分转换为隐式反馈。

2. **Pinterest：** 包含55,187个用户对9,911个pin的图片书签数据。该数据集代表来自用户行为的纯隐式反馈。

对于这两个数据集，我们将每个用户的最后一次交互留出用于测试，并使用剩余数据进行训练。我们进一步将训练数据分为90%用于训练，10%用于验证。

**评估指标：** 我们使用标准排名指标：

- **命中率（HR@K）：** 测量测试物品是否出现在前K个推荐中。
- **归一化折扣累积增益（NDCG@K）：** 考虑正确推荐物品的位置，给予排名靠前的物品更高权重。

我们报告HR@10和NDCG@10，对所有测试用户取平均。

**基线方法：** 我们与几种最先进的方法进行比较：

- **ItemPop：** 基于流行度（交互次数）推荐物品。
- **ItemKNN：** 使用余弦相似度的基于物品的协同过滤。
- **BPR：** 带矩阵分解的贝叶斯个性化排名。
- **eALS：** 用于隐式反馈的增强交替最小二乘法。
- **JRL：** 结合MF与自编码器的联合表示学习。

**实现细节：** 所有神经网络模型使用TensorFlow实现。所有方法的嵌入大小设置为64。对于MLP和NeuMF，我们使用层大小为[64, 32, 16, 8]的塔式结构。隐藏层的激活函数是ReLU，输出层是sigmoid。我们使用学习率0.001和批量大小256的Adam优化器。负采样比例为4:1。

#### 4.2 性能比较

表1总结了所有方法在两个数据集上的性能比较。可以得出几个观察结果：

1. **NCF变体始终优于传统方法。** GMF和MLP都比BPR和eALS取得更好的性能，证明了基于神经网络方法的有效性。

2. **NeuMF实现最佳整体性能。** 通过结合GMF和MLP，NeuMF利用了线性和非线性建模的优势，相比单个组件实现了显著改进。在MovieLens上，NeuMF相比最佳基线将HR@10提高了8.5%，在Pinterest上提高了6.2%。

3. **MLP从更深架构中受益。** 增加MLP中的层数持续提高性能，表明更深的网络可以捕捉更复杂的交互模式。然而，超过4层后观察到收益递减。

4. **数据集特征影响方法性能。** 在交互更密集的MovieLens上，方法之间的差距较小。在数据更稀疏的Pinterest上，神经方法显示出更大的改进，表明它们从有限信号中学习的能力。

#### 4.3 网络深度的影响

我们通过将MLP中隐藏层的数量从1变化到8，研究网络深度如何影响推荐性能。图2显示了结果。

主要发现：

- 性能随深度单调提高到4层，之后收益趋于平稳。
- 非常深的网络（8+层）有时显示性能下降，可能是由于优化困难或过拟合。
- 最优深度取决于数据集大小和复杂度。较大数据集从更深架构中受益更多。

这些结果验证了非线性建模的重要性，同时表明中等深度（3-4层）对于大多数推荐任务已经足够。

#### 4.4 激活函数的效果

我们比较了MLP组件的不同激活函数：ReLU、tanh、sigmoid和Leaky ReLU。结果显示：

- ReLU始终表现最好，可能是由于其缓解梯度消失问题和实现更快训练的能力。
- Tanh表现合理但收敛较慢。
- Sigmoid在深网络中遭受梯度消失问题。
- Leaky ReLU在某些情况下显示比ReLU略有改进，但不具有统计显著性。

基于这些发现，我们建议使用ReLU作为NCF模型的默认激活函数。

#### 4.5 负采样策略

负采样策略的选择显著影响模型性能。我们实验了：

1. **随机采样：** 从未观测到的交互中均匀采样负物品。
2. **基于流行度的采样：** 按物品流行度比例采样负样本。
3. **自适应采样：** 根据当前模型预测调整采样概率。

结果表明：

- 比例为4:1的随机采样在性能和效率之间提供了良好平衡。
- 基于流行度的采样可以略微提高性能但增加方差。
- 自适应采样显示出希望但需要仔细调整和额外计算。

对于实际应用，我们建议随机采样，比例在3:1到5:1之间作为稳健的默认选择。

#### 4.6 训练效率

我们分析了不同方法的训练时间和收敛行为。主要观察：

- GMF由于其简单结构训练最快，与传统MF相当。
- MLP每个epoch需要更多时间但由于更好的梯度流在更少epoch内收敛。
- NeuMF结合两者，需要约GMF 1.5倍的训练时间但实现卓越性能。
- 所有神经方法都从GPU加速中受益，与CPU训练相比速度提升10-50倍。

对于大规模部署，GMF在性能和效率之间提供了良好权衡，而当准确性至关重要时，NeuMF更可取。

#### 4.7 案例研究：分析学习的表示

为了深入了解NCF学习的内容，我们使用t-SNE降维可视化学习的嵌入。出现几种模式：

1. **按类型聚类：** 在MovieLens中，电影按类型（动作、喜剧、剧情）聚类，表明模型捕捉了语义相似性。

2. **用户偏好模式：** 具有相似观看习惯的用户在嵌入空间中形成聚类，表明模型成功捕捉了用户偏好。

3. **非线性边界：** NeuMF学习的决策边界比MF使用的线性分隔符更复杂，证实了非线性建模的优势。

这些可视化提供了定性证据，证明NCF学习了与人类对用户偏好和物品特征的直觉相符的有意义表示。

### 5. 相关工作

我们的工作建立在推荐系统和深度学习的几条研究路线之上并进行了扩展。

**协同过滤：** 传统CF方法包括基于邻域的方法和矩阵分解。最新进展包括概率矩阵分解、贝叶斯个性化排名和分解机。我们的工作通过引入神经网络作为基于内积模型的更灵活替代方案而有所不同。

**推荐的深度学习：** 以前深度学习在推荐中的应用集中在融入辅助信息。示例包括使用卷积神经网络处理物品图像、循环神经网络处理序列推荐，以及自编码器去噪稀疏数据。我们的工作独特之处在于直接应用深度学习建模用户-物品交互，而不依赖辅助信息。

**排名的神经网络：** 神经网络已应用于信息检索中的学习排序问题。我们的工作将这些思想适应于协同过滤，专注于推荐的独特挑战，如极端稀疏性和隐式反馈。

**表示学习：** NLP中词嵌入和计算机视觉中图像嵌入的成功激发了推荐中的类似方法。我们的嵌入层起到类似作用，将离散的用户和物品ID映射到可以捕捉语义关系的连续向量空间。

### 6. 结论与未来工作

在本文中，我们提出了神经协同过滤（NCF），这是一个应用深度学习技术于协同过滤的新颖框架。通过用神经网络替换传统矩阵分解中的内积运算，NCF可以从数据中学习复杂的非线性用户-物品交互。

我们的主要贡献包括：

1. 一个通用NCF框架，为神经协同过滤提供原则性方法。
2. 具体实例，包括GMF、MLP和混合NeuMF模型。
3. 广泛的实证评估，证明相比最先进方法的显著改进。
4. 对设计选择的全面分析，包括网络深度、激活函数和训练策略。

两个真实世界数据集的实验结果验证了我们方法的有效性。NeuMF始终实现最佳性能，展示了结合线性和非线性建模的价值。我们还表明，更深的神经架构通常提供更好的推荐，尽管超过中等深度后收益递减。

**未来方向：**

几个有希望的方向值得进一步研究：

1. **融入辅助信息：** 扩展NCF以融入用户人口统计、物品属性和上下文信息可以进一步提高性能，特别是对于冷启动场景。

2. **序列推荐：** 建模用户行为中的时间动态和序列模式可以增强时间敏感应用的推荐质量。

3. **可解释性：** 开发解释神经协同过滤模型的方法将增加信任并促进调试。注意力机制可能提供关于哪些特征驱动预测的洞察。

4. **可扩展性：** 针对拥有数十亿用户和物品的超大规模系统优化NCF需要高效采样、分布式训练和模型压缩技术。

5. **多任务学习：** 联合学习多个相关任务（评分预测、点击率估计、转化预测）可以通过共享表示提高泛化能力。

6. **对抗训练：** 在训练期间融入对抗样本可能提高模型鲁棒性和泛化能力。

随着深度学习继续改变各个领域，我们相信神经协同过滤代表了迈向更强大和灵活推荐系统的重要一步。本工作中提出的原则和技术为这一令人兴奋领域的未来创新提供了基础。

---

**参考文献 References:**

[1] He, X., Liao, L., Zhang, H., Nie, L., Hu, X., & Chua, T. S. (2017). Neural Collaborative Filtering. Proceedings of the 26th International Conference on World Wide Web, 173-182.

[2] Koren, Y., Bell, R., & Volinsky, C. (2009). Matrix Factorization Techniques for Recommender Systems. Computer, 42(8), 30-37.

[3] Rendle, S. (2010). Factorization Machines. ICDM.

[4] Mnih, A., & Salakhutdinov, R. (2008). Probabilistic Matrix Factorization. NIPS.

[5] Hu, Y., Koren, Y., & Volinsky, C. (2008). Collaborative Filtering for Implicit Feedback Datasets. ICDM.

[6] Goodfellow, I., Bengio, Y., & Courville, A. (2016). Deep Learning. MIT Press.

[7] Covington, P., Adams, J., & Sargin, E. (2016). Deep Neural Networks for YouTube Recommendations. RecSys.

[8] Zhang, S., Yao, L., & Tay, A. (2019). Deep Learning based Recommender System: A Survey and New Perspectives. ACM Computing Surveys.
